(ns docker-discovery.rest.core
  (:require [compojure.core :refer :all]
            [docker-discovery.rest.container :as container]
            [docker-discovery.rest.host :as host]
            [docker-discovery.util :as util]))

(defroutes rest-routes
  (GET "/" [] (util/json-response {:message "Welcome to the Docker Discovery REST api!"}))

  (context "/:host" [_]
    host/host-routes)

  (context "/:host/container" [_]
    container/container-routes))
