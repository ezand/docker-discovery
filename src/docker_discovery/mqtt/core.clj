(ns docker-discovery.mqtt.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? set-service-state]]))

(defn start []
  (when-not (started? :mqtt)
    (set-service-state :mqtt true)
    (log/info "MQTT service has started.")))

(defn stop []
  (when (started? :mqtt)
    (set-service-state :mqtt false)
    (log/info "MQTT service has stopped.")))
