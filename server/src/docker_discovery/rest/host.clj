(ns docker-discovery.rest.host
  (:require [compojure.core :refer [defroutes GET]]
            [docker-discovery.docker.host :as docker-host]
            [docker-discovery.util :as util]))

(defroutes host-routes
  (GET "/" [host]
    (when (util/docker-host-configured? host)
      (-> (merge (docker-host/info host)
                 (docker-host/version host)
                 (docker-host/ping host))
          (util/json-response))))

  (GET "/info" [host]
    (when (util/docker-host-configured? host)
      (util/json-response (docker-host/info host))))

  (GET "/ping" [host]
    (when (util/docker-host-configured? host)
      (util/json-response (docker-host/ping host))))

  (GET "/version" [host]
    (when (util/docker-host-configured? host)
      (util/json-response (docker-host/version host)))))
