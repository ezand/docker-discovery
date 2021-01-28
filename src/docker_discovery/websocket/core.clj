(ns docker-discovery.websocket.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? set-service-state]]))

(defn start []
  (when-not (started? :websocket)
    (set-service-state :websocket true)
    (log/info "Websocket service has started.")))

(defn stop []
  (when (started? :websocket)
    (set-service-state :websocket false)
    (log/info "Websocket service has stopped.")))
