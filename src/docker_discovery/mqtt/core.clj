(ns docker-discovery.mqtt.core
  (:require [clojurewerkz.machine-head.client :as client]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.util :as mqtt-util]
            [docker-discovery.system :refer [started? assoc-in-context dissoc-in-context
                                             service-context remove-service-context]]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg]
            [clojure.data.json :as json]
            [docker-discovery.docker.container :as container]
            [docker-discovery.docker.host :as host])
  (:import [org.eclipse.paho.client.mqttv3 IMqttClient]
           [java.util.concurrent TimeUnit]))

;; Want this loaded, even if it isn't used in this ns. By putting it
;; here it isn't subject to automatic cleanup of the ns form
(require 'docker-discovery.mqtt.home-assistant.core)

(defn- connect
  "Connect to the MQTT Broker."
  []
  (when-let [uri (cfg/get :mqtt :uri)]
    (let [connection (client/connect
                       (cfg/get :mqtt :uri)
                       {:opts (-> {:auto-reconnect true}
                                  (util/assoc-some :username (util/trim-to-nil (cfg/get :mqtt :username)))
                                  (util/assoc-some :password (util/trim-to-nil (cfg/get :mqtt :password))))})]
      (log/debug "MQTT client" (.getClientId connection) "is connected to broker" uri)
      connection)))

(defn- disconnect
  "Disconnect the client from the MQTT Broker."
  [^IMqttClient client]
  (when client
    (client/disconnect-and-close client)
    (log/debug "MQTT client" (.getClientId client) "is disconnected from broker" (cfg/get :mqtt :uri))))

(defn- refresh-state! []
  (when-let [mqtt-client (service-context :mqtt)]
    (let [{:keys [configuration attributes state]} (->> (mqtt-util/->state)
                                                        (group-by :type))]
      (doseq [{:keys [topic payload]} attributes]
        (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
      (doseq [{:keys [topic payload]} state]
        (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
      (doseq [{:keys [topic payload]} configuration]
        (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
      (log/trace "State refreshed and sent to MQTT"))))

(defn- handle-command [^String topic
                       {:keys [retained qos duplicate?]}
                       ^bytes payload]
  (when-let [mqtt-client (service-context :mqtt)]
    (when-some [command-value (mqtt-util/parse-command-value payload)]
      (let [{:keys [platform host container-name container-id]} (mqtt-util/parse-command-topic topic)
            operation-successful? (if command-value
                                    (container/start! host container-id)
                                    (container/stop! host container-id))]
        (when operation-successful?
          (let [host-info (-> (merge (host/info host)
                                     (host/version host)
                                     (host/ping host))
                              (util/trim-to-nil))
                device* (mqtt-util/device platform host host-info)
                container* {:id container-id :name container-name}
                state-topic (mqtt-util/switch-state-topic platform host container*)
                state-payload (mqtt-util/switch-state-payload platform host device* container*)]
            (client/publish mqtt-client state-topic (json/write-str state-payload :escape-slash false) 0 true)))))))

(def ^:private ^:const command-topic "homeassistant/+/+/+/set")

(defn- start-listening-for-commands []
  (when-let [mqtt-client (service-context :mqtt)]
    (client/subscribe mqtt-client {command-topic 0} handle-command)
    (log/debug "Listening for MQTT switch commands.")))

(defn- stop-listening-for-commands []
  (when-let [mqtt-client (service-context :mqtt)]
    (client/unsubscribe mqtt-client command-topic)
    (log/debug "Stopped listening for MQTT switch commands.")))

(defn start []
  (when-not (started? :mqtt)
    (->> (connect)
         (service-context :mqtt))
    (refresh-state!)
    (start-listening-for-commands)
    (when-let [refresh-interval-seconds (cfg/get :mqtt :refresh)]
      (->> (util/set-interval refresh-state! (.toMillis (TimeUnit/SECONDS) refresh-interval-seconds))
           (assoc-in-context :refresh-jobs [:mqtt]))
      (log/info "Scheduled a state refresh every" refresh-interval-seconds "second(s) to be sent to MQTT"))
    (log/info "MQTT service has started.")))

(defn stop []
  (when (started? :mqtt)
    (some-> (service-context :refresh-jobs)
            :mqtt
            (future-cancel))
    (stop-listening-for-commands)
    (dissoc-in-context :refresh-jobs [:mqtt])

    (-> (service-context :mqtt)
        (disconnect))
    (remove-service-context :mqtt)
    (log/info "MQTT service has stopped.")))
