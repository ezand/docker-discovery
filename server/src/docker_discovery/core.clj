(ns docker-discovery.core
  (:require [docker-discovery.config.core :as config]
            [docker-discovery.docker.event :as docker-events]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.core :as mqtt]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [docker-discovery.util :as util]
            [docker-discovery.web.core :as web]
            [omniconf.core :as cfg])
  (:gen-class))

(defn- add-shutdown-hook [shutdown-fn]
  (let [thread (Thread. ^Runnable shutdown-fn)]
    (.addShutdownHook (Runtime/getRuntime) thread)
    (log/debug "Added shutdown hook.")))

(defn stop []
  (when (started?)
    (docker-events/stop)
    (web/stop)
    (mqtt/stop)

    (remove-service-context :main)
    (System/gc)
    (log/info "Docker Discovery has stopped.")))

(defn start []
  (when-not (started?)
    (add-shutdown-hook stop)
    (config/load-config)
    (docker-events/start)
    (when (or (util/exposure-enabled? :rest)
              (util/exposure-enabled? :websocket))
      (web/start))
    (when (util/exposure-enabled? :mqtt)
      (mqtt/start))

    (log/set-log-level! (cfg/get :log-level))
    (service-context :main true)
    (log/info "Docker Discovery has started.")))

(defn restart []
  (when (started?)
    (stop))
  (start))

(defn -main
  "Main entrypoint that's run on application startup."
  [& _]
  (start))
