(ns docker-discovery.rest.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.user :refer [started? set-state]]))

(defn start []
  (when-not (started? :rest)
    (set-state :rest true)
    (log/info "REST service has started.")))

(defn stop []
  (when (started? :rest)
    (set-state :rest false)
    (log/info "REST service has stopped.")))
