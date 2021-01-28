(ns docker-discovery.core
  (:require [docker-discovery.config.core :as config]
            [docker-discovery.log :as log])
  (:gen-class))

(defonce system (atom {}))

(defn started? []
  (:started? @system))

(defn start [& args]
  (when-not (started?)
    (config/load-config)

    (reset! system (assoc @system :started? true))
    (log/info "Docker Discovery has started.")))

(defn stop []
  (when (started?)
    (reset! system (assoc @system :started? false))
    (log/info "Docker Discovery has stopped.")))

(defn restart []
  (when (started?)
    (stop)
    (start)))

(defn -main
  "Main entrypoint that's run on application startup."
  [& args]
  (start args))
