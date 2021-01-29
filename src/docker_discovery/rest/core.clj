(ns docker-discovery.rest.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes rest-routes
  (GET "/" [] "Welcome to the Docker Discovery REST api!"))
