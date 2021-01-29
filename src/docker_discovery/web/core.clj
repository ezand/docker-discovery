(ns docker-discovery.web.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [docker-discovery.log :as log]
            [docker-discovery.rest.core :as rest]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [immutant.web :as web]
            [omniconf.core :as cfg]
            [docker-discovery.util :as util]))

(defroutes app
  (GET "/" [] "Welcome to Docker Discovery!")

  (when (util/exposure-enabled? :rest)
    (context "/api" [_]
      rest/rest-routes))

  (route/not-found "Not found"))

(defn start []
  (when-not (started? :web)
    (->> (web/run app {:port (cfg/get :web :port)})
         (service-context :web))

    (log/info "Web service has started.")
    (when (util/exposure-enabled? :rest)
      (log/info "REST api is enabled."))))

(defn stop []
  (when (started? :web)
    (-> (service-context :web)
        (web/stop))

    (remove-service-context :web)
    (log/info "Web service has stopped.")))
