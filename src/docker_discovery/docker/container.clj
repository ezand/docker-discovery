(ns docker-discovery.docker.container
  (:require [clojure.data.json :as json]
            [docker-discovery.docker.core :as docker]))

;;;;;;;;;;;;;
;; Filters ;;
;;;;;;;;;;;;;
(defn id-filter [container-id]
  (when container-id {:id [container-id]}))

(defn id-filter [container-name]
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
  (->> (search-request filters)
       (docker/invoke host :containers)))

(defn find-all [host]
  (search host {}))

(defn find-by-id [host id]
  (some->> (id-filter id)
           (search host)
           (first)))

(defn start! [host id])

(defn stop! [host id])

(defn restart! [host id])
