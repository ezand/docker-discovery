(ns docker-discovery.web.authentication
  (:require [omniconf.core :as cfg]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [docker-discovery.util :as util]
            [docker-discovery.log :as log]))

(def ^:private ^:const rest-path-regex #"^(\/api$|\/api\/.*$)")
(def ^:private ^:const websocket-path-regex #"^(\/ws$|\/ws\/.*$)")

(defn authenticated? [service username password]
  (if (cfg/get service :username)
    (and (= username (cfg/get service :username))
         (= password (cfg/get service :password)))
    true))

(defn wrap-http-authentication [handler]
  (if (and (or (util/exposure-enabled? :rest)
               (util/exposure-enabled? :websocket))
           (cfg/get :http :username))
    (fn [{:keys [uri] :as request}]
      (if (or (re-matches rest-path-regex uri)
              (re-matches websocket-path-regex uri))
        (let [secure-handler (wrap-basic-authentication handler (partial authenticated? :http))]
          (log/trace "Handling secure REST endpoint:" uri)
          (secure-handler request))
        (handler request)))
    handler))
