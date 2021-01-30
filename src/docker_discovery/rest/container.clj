(ns docker-discovery.rest.container
  (:require [compojure.core :refer :all]
            [docker-discovery.docker.container :as docker-container]
            [docker-discovery.util :as util]))

(defroutes container-routes
  (GET "/" [host runningOnly]
    (when (util/docker-host-configured? host)
      (util/json-response (->> (util/str->boolean runningOnly)
                               (docker-container/find-all host)
                               (util/trim-to-empty)))))

  (GET "/:container-id" [host container-id]
    (when (util/docker-host-configured? host)
      (util/json-response (docker-container/find-by-id host container-id)))))
