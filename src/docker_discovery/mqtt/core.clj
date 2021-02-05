(ns docker-discovery.mqtt.core
  (:require [clojure.data.json :as json]
            [clojurewerkz.machine-head.client :as client]
            [docker-discovery.docker.container :as container]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.util :as mqtt-util]
            [docker-discovery.system :refer [started? assoc-in-context dissoc-in-context
                                             service-context remove-service-context]]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg])
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
      (future (do (doseq [{:keys [topic payload]} attributes]
                    (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
                  (doseq [{:keys [topic payload]} state]
                    (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
                  (doseq [{:keys [topic payload]} configuration]
                    (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))))
      (log/trace "State refreshed and sent to MQTT"))))

;;;;;;;;;;;;;;;;;;;
;; Docker Events ;;
;;;;;;;;;;;;;;;;;;;
(def ^:const docker-event-types #{:create :destroy :rename :start :stop :pause :unpause})

(defmulti handle-container-event :status)

(defmethod handle-container-event :default [{:keys [status local-name id actor] :as event} host-info]
  (when (#{:start :stop} status)
    (let [container-name (get-in actor [:attributes :name])]
      (doseq [platform (cfg/get :mqtt :platforms)]
        (mqtt-util/publish-switch-state-update platform local-name container-name id status)))))

; TODO handle more event statuses

;;;;;;;;;;;;;;;;;
;; MQTT Events ;;
;;;;;;;;;;;;;;;;;
(defn- handle-command [^String topic
                       {:keys [retained qos duplicate?]}
                       ^bytes payload]
  (when-some [command-value (mqtt-util/parse-command-value payload)]
    (let [{:keys [platform host container-name container-id]} (mqtt-util/parse-command-topic topic)
          operation-successful? (if command-value
                                  (container/start! host container-id)
                                  (container/stop! host container-id))]
      (when operation-successful?
        (mqtt-util/publish-switch-state-update platform host container-name container-id
                                               (util/boolean->container-state command-value))))))

(def ^:private ^:const command-topic "+/+/+/+/set")

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
