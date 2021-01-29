(ns docker-discovery.web.authentication
  (:require [omniconf.core :as cfg]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [docker-discovery.util :as util]
            [docker-discovery.log :as log]))

(def ^:private ^:const rest-path-regex #"^(\/api$|\/api\/.*$)")

(defn authenticated? [service username password]
  (if (cfg/get service :username)
    (and (= username (cfg/get service :username))
         (= password (cfg/get service :password)))
    true))

(defn wrap-rest-authentication [handler]
  (if (and (util/exposure-enabled? :rest)
           (cfg/get :rest :username))
    (fn [{:keys [uri] :as request}]
      (if (re-matches rest-path-regex uri)
        (let [secure-handler (wrap-basic-authentication handler (partial authenticated? :rest))]
          (log/trace "Handling secure REST endpoint:" uri)
          (secure-handler request))
        (handler request)))
    handler))
