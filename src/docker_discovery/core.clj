(ns docker-discovery.core
  (:require [docker-discovery.config.core :as config])
  (:gen-class))

(defonce system (atom {}))

(defn started? []
  (:started? @system))

(defn start [& args]
  (when-not (started?)
    (config/load-config)

    (reset! system (assoc @system :started? true))))

(defn stop []
  (when (started?)
    (reset! system (assoc @system :started? false))))

(defn restart []
  (when (started?)
    (stop)
    (start)))

(defn -main
  "Main entrypoint that's run on application startup."
  [& args]
  (start args))
