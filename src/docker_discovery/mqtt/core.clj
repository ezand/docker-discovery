(ns docker-discovery.mqtt.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]))

(defn start []
  (when-not (started? :mqtt)
    (service-context :mqtt true)
    (log/info "MQTT service has started.")))

(defn stop []
  (when (started? :mqtt)
    (remove-service-context :mqtt)
    (log/info "MQTT service has stopped.")))
