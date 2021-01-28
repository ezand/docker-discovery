(ns docker-discovery.config.env
  (:require [docker-discovery.util :as util]
            [superstring.core :as str]
            [medley.core :as medley]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Environment variables ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def ^:private ^:const docker-property-regex #"HOST_(.*)_(.*)")
(def ^:const CONFIG_FILE "CONFIG_FILE")
(def ^:const DOCKER_EXPOSURE "DOCKER_EXPOSURE")
(def ^:const HTTP_PORT "HTTP_PORT")
(def ^:const HTTP_USERNAME "HTTP_USERNAME")
(def ^:const HTTP_PASSWORD "HTTP_PASSWORD")
(def ^:const MQTT_URI "MQTT_URI")
(def ^:const MQTT_USERNAME "MQTT_USERNAME")
(def ^:const MQTT_PASSWORD "MQTT_PASSWORD")
(def ^:const MQTT_REFRESH "MQTT_REFRESH")
(def ^:const WEBSOCKET_REFRESH "WEBSOCKET_REFRESH")

;;;;;;;;;;;;;;;;;;;
;; Default props ;;
;;;;;;;;;;;;;;;;;;;
(def ^:private ^:const docker-host-defaults
  {:events true})

(def ^:private ^:const mqtt-defaults
  {:refresh 3600})

(def ^:private ^:const websocket-defaults
  {:refresh 3600})

;;;;;;;;;;;
;; Utils ;;
;;;;;;;;;;;
(defn str-value [key]
  (some-> (System/getenv key) (str/trim)))

(defn- int-value [key]
  (try
    (some-> (System/getenv key) (str/trim) (Integer/parseInt))
    (catch Throwable _ nil)))

(defn- keywordize-all [xs]
  (map keyword xs))

(defn- host-property [k]
  (when (string? k)
    (when-let [[_ host key] (re-matches docker-property-regex k)]
      [(keyword (str/lower-case host))
       (keyword (str/lower-case key))])))

(defn- merge-defaults [orig defaults]
  (merge defaults orig))

;;;;;;;;;;;;;;;;
;; Properties ;;
;;;;;;;;;;;;;;;;
(defmulti docker-host-property-value (fn [k _] k))
(defmethod docker-host-property-value :default [_ v] v)

(defmethod docker-host-property-value :events [_ v]
  (if-not (util/trim-to-nil v)
    true
    (util/str->boolean v)))

(defn- docker-host [acc [k v]]
  (let [[host key] (host-property k)
        host-props (get acc host {})]
    (assoc acc host (assoc host-props key (docker-host-property-value key v)))))

(defn- docker-hosts []
  (->> (System/getenv)
       (medley/filter-keys #(re-matches docker-property-regex %))
       (reduce docker-host {})
       (merge docker-host-defaults)
       (util/trim-to-nil)))

(defn- docker-props []
  (-> {}
      (util/assoc-some :hosts (docker-hosts))
      (util/trim-to-nil)))

(defn- docker-exposure []
  (some-> (str-value DOCKER_EXPOSURE)
          (str)
          (str/trim)
          (str/split #",\s*")
          (keywordize-all)
          (set)
          (util/trim-to-nil)))

(defn- rest-props []
  (-> {}
      (util/assoc-some :port (int-value HTTP_PORT))
      (util/assoc-some :username (str-value HTTP_USERNAME))
      (util/assoc-some :password (str-value HTTP_PASSWORD))
      (util/trim-to-nil)))

(defn- mqtt-props []
  (-> {}
      (util/assoc-some :refresh (str-value MQTT_REFRESH))
      (util/assoc-some :uri (str-value MQTT_URI))
      (util/assoc-some :username (str-value MQTT_USERNAME))
      (util/assoc-some :password (str-value MQTT_PASSWORD))
      (merge-defaults mqtt-defaults)
      (util/trim-to-nil)))

(defn- websocket-props []
  (-> {}
      (util/assoc-some :refresh (str-value WEBSOCKET_REFRESH))
      (merge-defaults websocket-defaults)
      (util/trim-to-nil)))

(defn env->config []
  (-> {}
      (util/assoc-some :docker (docker-props))
      (util/assoc-some :docker-exposure (docker-exposure))
      (util/assoc-some :rest (rest-props))
      (util/assoc-some :mqtt (mqtt-props))
      (util/assoc-some :websocket (websocket-props))))
