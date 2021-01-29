(ns docker-discovery.rest.core
  (:require [compojure.core :refer :all]))

(defroutes rest-routes
  (GET "/" [] "Welcome to the Docker Discovery REST api!"))
