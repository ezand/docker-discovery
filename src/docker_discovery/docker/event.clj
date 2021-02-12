(ns docker-discovery.docker.event
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [docker-discovery.docker.core :as docker]
            [docker-discovery.docker.host :as host]
            [docker-discovery.log :as log]
            [docker-discovery.mqtt.core :as mqtt]
            [docker-discovery.system :refer [dissoc-in-context assoc-in-context service-context remove-service-context]]
            [docker-discovery.util :as util]
            [docker-discovery.websocket.outgoing :as websocket]
            [medley.core :as medley]
            [omniconf.core :as cfg])
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
(defmethod handle-event :container [{:keys [status] :as event} host]
  (log/trace "Handling container event on host" host ":" event)

  (when (not-empty (cfg/get :docker :exposure))
    (let [event* (-> (update event :status keyword)
                     (assoc :local-name host))
          host-info (host/info host)]
      (when (and (util/exposure-enabled? :mqtt)
                 (contains? mqtt/docker-event-types (keyword status)))
        (mqtt/handle-container-event event* host-info))

      (when (and (util/exposure-enabled? :websocket)
                 (contains? websocket/docker-event-types (keyword status)))
        (websocket/handle-container-event event* host-info)))))

(defn- listening? [host listener-id]
  (= (get (service-context :docker-events) (keyword host)) listener-id))

(defn- open-stream [host listener-id stream]
  (with-open [rdr (io/reader stream)]
    (log/info "Started listening for Docker events on host" (name host))
    (loop [r (BufferedReader. rdr)]
      (when-let [line (.readLine r)]
        (let [event (json/read-str line :key-fn keyword)]
          (when (map? event)
            (handle-event (-> (util/lispy-keys event)
                              (update :type keyword)) host))
          (when (listening? host listener-id)
            (recur r)))))))

(defn listen [host]
  (if (and (cfg/get :docker :hosts (keyword host) :events)
           (not (contains? (service-context :docker-events) (keyword host))))
    (let [listener-id (gensym "container_listener_")]
      (->> (event-stream host)
           (open-stream host listener-id)
           (future))
      (assoc-in-context :docker-events [(keyword host)] listener-id))
    (service-context :docker-events)))

(defn unlisten [host]
  (dissoc-in-context :docker-events [(keyword host)]))

(defn start []
  (doseq [host (->> (cfg/get :docker :hosts)
                    (medley/filter-vals :events)
                    (keys))]
    (listen host)))

(defn stop []
  (remove-service-context :docker-events))
