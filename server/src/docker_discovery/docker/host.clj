(ns docker-discovery.docker.host
  (:require [docker-discovery.docker.core :as docker]))

;;;;;;;;;;;;;;
;; Requests ;;
;;;;;;;;;;;;;;
(defn- info-request []
  {:op :SystemInfo})

(defn- version-request []
  {:op :SystemVersion})

(defn- ping-request []
  {:op :SystemPing})

;;;;;;;;;;;;;;;
;; Endpoints ;;
;;;;;;;;;;;;;;;
(defn info [host]
  (docker/invoke host :info (info-request)))

(defn version [host]
  (docker/invoke host :version (version-request)))

(defn ping [host]
  {:ping (docker/invoke host :_ping (ping-request))})
