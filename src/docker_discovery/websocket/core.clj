(ns docker-discovery.websocket.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]))

(defn start []
  (when-not (started? :websocket)
    (service-context :websocket true)
    (log/info "Websocket service has started.")))

(defn stop []
  (when (started? :websocket)
    (remove-service-context :websocket)
    (log/info "Websocket service has stopped.")))
