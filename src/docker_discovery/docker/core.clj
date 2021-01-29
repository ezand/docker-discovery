(ns docker-discovery.docker.core
  (:require [clj-docker-client.core :as docker]
            [omniconf.core :as cfg]
            [docker-discovery.log :as log]
            [docker-discovery.util :as util]))

(defn- load-client [{:keys [host uri category]}]
  (log/debug "Creating new Docker" (name category) "client for host" (name host) "on" uri)
  (docker/client {:category category
                  :conn {:uri uri}
                  :api-version (cfg/get :docker :api-version)}))

(def ^:private client (memoize load-client))

(defn- client-context [host category]
  (some-> (cfg/get :docker :hosts host)
          (util/assoc-some :host host
                           :category category)))

(defn invoke
  ([host category request]
   (invoke host category false request))
  ([host category stream? request]
   (let [result (some-> (client-context (keyword host) category)
                        (client)
                        (docker/invoke request))]
     (if stream?
       (.getInputStream result)
       (util/lispy-keys result)))))
