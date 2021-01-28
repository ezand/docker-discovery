(ns docker-discovery.rest.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [immutant.web :as web]
            [omniconf.core :as cfg]))

(defn app [request]
  {:status 200
   :body "Hello world!"})

(defn start []
  (when-not (started? :rest)
    (->> (web/run app {:port (cfg/get :rest :port)})
         (service-context :rest))

    (log/info "REST service has started.")))

(defn stop []
  (when (started? :rest)
    (-> (service-context :rest)
        (web/stop))

    (remove-service-context :rest)
    (log/info "REST service has stopped.")))
