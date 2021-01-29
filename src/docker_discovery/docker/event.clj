(ns docker-discovery.docker.event
  (:require [docker-discovery.docker.core :as docker]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [medley.core :as medley]
            [omniconf.core :as cfg]))

;;;;;;;;;;;;;;
;; Requests ;;
;;;;;;;;;;;;;;
(defn- system-events-request []
  {:op :SystemEvents
   :as :socket})

;;;;;;;;;;;;;;;
;; Endpoints ;;
;;;;;;;;;;;;;;;
(defn event-stream [host]
  (some->> (system-events-request)
           (docker/invoke host :events)
           (.getInputStream)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Docker events handling ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti handle-event :type)
(defmethod handle-event :default [event host]
  (log/trace "Unhandled Docker event on host" host ":" event))
(defmethod handle-event :container [event host]
  (log/trace "Handling container event on host" host ":" event))

(defn listen [host]
  (let [listeners (service-context :docker-events)]
    (if (and (cfg/get :docker :hosts (keyword host) :events)
             (not (contains? listeners (keyword host))))
      (let [stream (event-stream host)
            listener-id (gensym "container_listener_")]
        (update listeners (keyword host) #(conj % listener-id)))
      listeners)))

(defn unlisten [host]
  (let [updated (-> (service-context :docker-events)
                    (dissoc (keyword host)))]
    (service-context :docker-events updated)))

(defn start []
  (->> (for [host (->> (cfg/get :docker :hosts)
                       (medley/filter-vals :events)
                       (keys))]
         (listen host))
       (into {})
       (service-context :docker-events)))

(defn stop []
  (remove-service-context :docker-events))
