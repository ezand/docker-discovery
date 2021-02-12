(ns docker-discovery.web.core
  (:require [compojure.core :refer [defroutes GET context]]
            [compojure.route :as route]
            [docker-discovery.util :as util]
            [docker-discovery.log :as log]
            [docker-discovery.rest.core :as rest]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [docker-discovery.web.authentication :as auth]
            [docker-discovery.websocket.core :as websocket]
            [immutant.web :as web]
            [omniconf.core :as cfg]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app
  (GET "/" [] (util/json-response {:message "Welcome to Docker Discovery!"}))

  (when (util/exposure-enabled? :rest)
    (context "/api" [_]
      rest/rest-routes))

  (route/not-found (util/json-response {:message "Endpoint not found"})))

(defn start []
  (when-not (started? :web)
    (->> (web/run (-> (wrap-defaults app site-defaults)
                      (auth/wrap-http-authentication)
                      (websocket/wrap-ws))
                  {:port (cfg/get :web :port)})
         (service-context :web))

    (log/info "Web service has started.")
    (when (util/exposure-enabled? :rest)
      (log/info "REST api is enabled."))
    (when (util/exposure-enabled? :websocket)
      (websocket/start)
      (log/info "WebSocket support is enabled."))))

(defn stop []
  (when (started? :web)
    (websocket/stop)
    (-> (service-context :web)
        (web/stop))

    (remove-service-context :web)
    (log/info "Web service has stopped.")))
