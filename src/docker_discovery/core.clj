(ns docker-discovery.core
  (:require [docker-discovery.config.core :as config]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.core :as mqtt]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [docker-discovery.util :as util]
            [docker-discovery.web.core :as web]
            [omniconf.core :as cfg])
  (:gen-class))

(defn start [& args]
  (when-not (started?)
    (config/load-config)
    (when (or (util/exposure-enabled? :rest)
              (util/exposure-enabled? :websocket))
      (web/start))
    (when (util/exposure-enabled? :mqtt)
      (mqtt/start))

    (log/set-log-level! (cfg/get :log-level))
    (service-context :main true)
    (log/info "Docker Discovery has started.")))

(defn stop []
  (when (started?)
    (web/stop)
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
