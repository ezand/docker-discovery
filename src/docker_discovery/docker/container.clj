(ns docker-discovery.docker.container
  (:require [clojure.data.json :as json]
            [docker-discovery.docker.core :as docker]
            [docker-discovery.util :as util]
            [superstring.core :as str]))

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

;;;;;;;;;;;;;
;; Filters ;;
;;;;;;;;;;;;;
(defn id-filter [container-id]
  (when container-id {:id [container-id]}))

(defn name-filter [container-name]
  (when container-name {:name [container-name]}))

;;;;;;;;;;;;;;
;; Requests ;;
;;;;;;;;;;;;;;
(defn- search-request [filters]
  {:op :ContainerList
   :params {:all true
            :filters (json/write-str filters)}})

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

(defn find-all [host]
  (search host {}))

(defn find-by-id [host id]
  (some->> (id-filter id)
           (search host)
           (first)))

(defn start! [host id]
  (some->> (start-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))

(defn stop! [host id]
  (some->> (stop-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))

(defn restart! [host id]
  (some->> (restart-request id)
           (docker/invoke host :containers)
           (container-operation-successful?)))
