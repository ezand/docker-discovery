(ns docker-discovery.mqtt.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.user :refer [started? set-state]]))

(defn start []
  (when-not (started? :mqtt)
    (set-state :mqtt true)
    (log/info "MQTT service has started.")))

(defn stop []
  (when (started? :mqtt)
    (set-state :mqtt false)
    (log/info "MQTT service has stopped.")))
