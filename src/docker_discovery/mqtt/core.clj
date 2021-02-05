(ns docker-discovery.mqtt.core
  (:require [clojurewerkz.machine-head.client :as client]
            [docker-discovery.log :as log]
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

(defn refresh-state! []
  (let [state {}]
    ; TODO
    (log/trace "State refreshed and sent to MQTT")))

(defn start []
  (when-not (started? :mqtt)
    (->> (connect)
         (service-context :mqtt))
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
    (dissoc-in-context :refresh-jobs [:mqtt])

    (-> (service-context :mqtt)
        (disconnect))
    (remove-service-context :mqtt)
    (log/info "MQTT service has stopped.")))
