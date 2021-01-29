(ns docker-discovery.docker.container
  (:require [clojure.data.json :as json]
            [docker-discovery.docker.core :as docker]
            [docker-discovery.util :as util]))

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

(defn start! [host id])

(defn stop! [host id])

(defn restart! [host id])
