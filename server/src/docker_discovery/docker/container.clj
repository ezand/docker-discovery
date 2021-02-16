(ns docker-discovery.docker.container
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [docker-discovery.docker.core :as docker]
            [docker-discovery.log :as log]
            [docker-discovery.util :as util]
            [superstring.core :as str])
  (:import [java.io BufferedReader]))

;;;;;;;;;;;
;; Utils ;;
;;;;;;;;;;;
(defn- assoc-names [container]
  (when container
    (-> container
        (util/assoc-some
          :name (util/container-name container)
          :additional-names (util/container-additional-names container))
        (dissoc :names))))

(defn- container-operation-successful? [result]
  (and (string? result)
       (str/blank? result)))

(defn- read-stats-snapshot [stream]
  (with-open [rdr (io/reader stream)]
    (when-let [line (.readLine (BufferedReader. rdr))]
      (json/read-str line :key-fn keyword))))

;;;;;;;;;;;;;
;; Filters ;;
;;;;;;;;;;;;;
(defn id-filter [container-id]
  (when container-id {:id [container-id]}))

(defn name-filter [container-name]
  (when container-name {:name [container-name]}))

(defn running-only-filter []
  {:status ["running"]})

;;;;;;;;;;;;;;
;; Requests ;;
;;;;;;;;;;;;;;
(defn- search-request [filters]
  {:op :ContainerList
   :params {:all true
            :filters (json/write-str filters)}})

(defn- stats-request [id]
  {:op :ContainerStats
   :params {:id id}
   :as :stream})

(defn- start-request [id]
  {:op :ContainerStart
   :params {:id id}})

(defn- stop-request [id]
  {:op :ContainerStop
   :params {:id id}})

(defn- restart-request [id]
  {:op :ContainerRestart
   :params {:id id}})

;;;;;;;;;;;;;;;
;; Endpoints ;;
;;;;;;;;;;;;;;;
(defn search [host filters]
  (some->> (search-request filters)
           (docker/invoke host :containers)
           (map assoc-names)))

(defn find-all
  ([host running-only?]
   (if running-only?
     (search host (running-only-filter))
     (search host {})))
  ([host]
   (find-all host false)))

(defn find-by-id [host id]
  (some->> (id-filter id)
           (search host)
           (first)))

(defn find-by-name [host name]
  (some->> (name-filter name)
           (search host)
           (first)))

(defn stats [host id]
  (some->> (stats-request id)
           (docker/invoke host :containers)
           (read-stats-snapshot)))

(defn start! [host id]
  (log/trace "Starting container" id "on host" (name host))
  (some->> (start-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))

(defn stop! [host id]
  (log/trace "Stopping container" id "on host" (name host))
  (some->> (stop-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))

(defn restart! [host id]
  (log/trace "Restarting container" id "on host" (name host))
  (some->> (restart-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))
