(ns docker-discovery.core
  (:require [docker-discovery.config.core :as config]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.core :as mqtt]
            [docker-discovery.rest.core :as rest]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [docker-discovery.util :as util]
            [docker-discovery.websocket.core :as websocket])
  (:gen-class))

(defn start [& args]
  (when-not (started?)
    (config/load-config)
    (when (util/exposure-enabled? :rest)
      (rest/start))
    (when (util/exposure-enabled? :websocket)
      (websocket/start))
    (when (util/exposure-enabled? :mqtt)
      (mqtt/start))

    (service-context :main true)
    (log/info "Docker Discovery has started.")))

(defn stop []
  (when (started?)
    (rest/stop)
    (websocket/stop)
    (mqtt/stop)

    (remove-service-context :main)
    (log/info "Docker Discovery has stopped.")))

(defn restart []
  (when (started?)
    (stop))
  (start))

(defn -main
  "Main entrypoint that's run on application startup."
  [& args]
  (start args))
