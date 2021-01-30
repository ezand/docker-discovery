(ns docker-discovery.rest.health
  (:require [compojure.core :refer :all]
            [docker-discovery.util :as util]
            [docker-discovery.system :refer [service-context]]
            [omniconf.core :as cfg]))

(defmulti exposure-health (fn [exposure] (keyword exposure)))
(defmethod exposure-health :default [_] nil)

(defmethod exposure-health :system [_]
  {:system true})

(defmethod exposure-health :config [_]
  (try {:config (nil? (cfg/verify :silent true))}
       (catch Throwable _ false)))

(defmethod exposure-health :mqtt [_]
  (let [health (when (util/exposure-enabled? :mqtt)
                 (or (some-> (service-context :mqtt)
                             (.isConnected))
                     false))]
    (-> (util/assoc-some {} :mqtt health)
        (util/trim-to-nil))))

(defmethod exposure-health :websocket [_]
  (let [client-count (when (util/exposure-enabled? :websocket)
                       (-> (service-context :websocket)
                           (count)))]
    (-> (util/assoc-some {} :websocket client-count)
        (util/trim-to-nil))))

(defroutes health-routes
  (GET "/" []
    (-> (exposure-health :system)
        (merge (exposure-health :config)
               (exposure-health :mqtt)
               (exposure-health :websocket))
        (util/camelize-keys)
        (util/json-response)))

  (GET "/:exposure" [exposure]
    (some-> (exposure-health exposure)
            (util/trim-to-nil)
            (util/camelize-keys)
            (util/json-response))))
