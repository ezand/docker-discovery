(ns docker-discovery.websocket.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.user :refer [started? set-state]]))

(defn start []
  (when-not (started? :websocket)
    (set-state :websocket true)
    (log/info "Websocket service has started.")))

(defn stop []
  (when (started? :websocket)
    (set-state :websocket false)
    (log/info "Websocket service has stopped.")))
