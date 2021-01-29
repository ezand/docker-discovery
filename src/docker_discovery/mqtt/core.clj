(ns docker-discovery.mqtt.core
  (:require [clojurewerkz.machine-head.client :as client]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg])
  (:import [org.eclipse.paho.client.mqttv3 IMqttClient]))

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

(defn start []
  (when-not (started? :mqtt)
    (->> (connect)
         (service-context :mqtt))
    (log/info "MQTT service has started.")))

(defn stop []
  (when (started? :mqtt)
    (-> (service-context :mqtt)
        (disconnect))

    (remove-service-context :mqtt)
    (log/info "MQTT service has stopped.")))
