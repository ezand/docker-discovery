(ns docker-discovery.rest.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? set-service-state]]))

(defn start []
  (when-not (started? :rest)
    (set-service-state :rest true)
    (log/info "REST service has started.")))

(defn stop []
  (when (started? :rest)
    (set-service-state :rest false)
    (log/info "REST service has stopped.")))
