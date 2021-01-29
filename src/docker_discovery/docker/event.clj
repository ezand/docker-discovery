(ns docker-discovery.docker.event
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [docker-discovery.docker.core :as docker]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context remove-service-context]]
            [medley.core :as medley]
            [omniconf.core :as cfg]
            [docker-discovery.util :as util])
  (:import [java.io BufferedReader]))

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
           (docker/invoke host :events true)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Docker events handling ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmulti handle-event :type)
(defmethod handle-event :default [event host]
  (log/trace "Unhandled Docker event on host" host ":" event))
(defmethod handle-event :container [event host]
  (log/trace "Handling container event on host" host ":" event))

(defn listen [host]
  (if (and (cfg/get :docker :hosts (keyword host) :events)
           (not (contains? (service-context :docker-events) (keyword host))))
    (let [stream (event-stream host)
          listener-id (gensym "container_listener_")]
      (future
        (with-open [rdr (io/reader stream)]
          (log/info "Started listening for Docker events on host" (name host))
          (loop [r (BufferedReader. rdr)]
            (when-let [line (.readLine r)]
              (let [event (json/read-str line :key-fn keyword)]
                (when (map? event)
                  (handle-event (-> (util/lispy-keys event)
                                    (update :type keyword)) host))
                (when (= (get (service-context :docker-events) (keyword host)) listener-id)
                  (recur r)))))))
      (service-context :docker-events (assoc (service-context :docker-events) (keyword host) listener-id)))
    (service-context :docker-events)))

(defn unlisten [host]
  (let [updated (-> (service-context :docker-events)
                    (dissoc (keyword host)))]
    (service-context :docker-events updated)))

(defn start []
  (doseq [host (->> (cfg/get :docker :hosts)
                    (medley/filter-vals :events)
                    (keys))]
    (listen host)))

(defn stop []
  (remove-service-context :docker-events))
