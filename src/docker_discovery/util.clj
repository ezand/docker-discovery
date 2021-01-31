(ns docker-discovery.util
  (:require [superstring.core :as str]
            [clojure.java.io :as io]
            [omniconf.core :as cfg]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [clojure.set :as set])
  (:import [java.util Map Map$Entry]))

;;;;;;;;;;;;;;;;;;;
;; General utils ;;
;;;;;;;;;;;;;;;;;;;
; implement custom reduce-kv implementation for (System/getenv) UnmodifiableMap
(extend-protocol clojure.core.protocols/IKVReduce
  Map
  (kv-reduce [m f init]
    (let [iter (.. m entrySet iterator)]
      (loop [ret init]
        (if (.hasNext iter)
          (let [^Map$Entry kv (.next iter)]
            (recur (f ret (.getKey kv) (.getValue kv))))
          ret)))))

(defn assoc-some
  "Like `assoc` but only if `v` is not `nil`."
  ([m k v] (if (nil? v) m (assoc m k v)))
  ([m k v & kvs]
   (assert (even? (count kvs)))
   (let [it (.iterator ^Iterable kvs)]
     (loop [m (assoc-some m k v)]
       (if (.hasNext it)
         (recur (assoc-some m (.next it) (.next it)))
         m)))))

(defn assoc-some-in
  "Like `assoc-in` but only if `v` is not `nil`."
  ([m ks v] (if (nil? v) m (assoc-in m ks v)))
  ([m ks v & kvs]
   (assert (even? (count kvs)))
   (let [it (.iterator ^Iterable kvs)]
     (loop [m (assoc-some-in m ks v)]
       (if (.hasNext it)
         (recur (assoc-some-in m (.next it) (.next it)))
         m)))))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn contains-in?
  "Like `contains?` but also supports nested keys."
  [m ks]
  (contains? (get-in m (butlast ks)) (last ks)))

(defn update-existing-in
  "Like `update-in` but only if `m` contains `ks`."
  [m ks f & args]
  (if (contains-in? m ks)
    (apply update-in m ks f args)
    m))

(defn str->boolean [s]
  (Boolean/parseBoolean (str s)))

(defn trim-to-nil [x]
  (cond (string? x) (let [trimmed (str/trim x)]
                      (if-not (str/blank? trimmed)
                        trimmed
                        nil))
        (or (map? x)
            (coll? x)) (if (not-empty x) x nil)
        :else nil))

(defn trim-to-empty [xs]
  (if-let [coll (seq (remove nil? xs))]
    coll
    []))

(defn file [file-location]
  (when-let [file (io/file file-location)]
    (when (.exists file) file)))

(defn exposure-enabled? [exposure]
  (get (cfg/get :docker-exposure) exposure))

(defn- camelize-keys*
  "Rename the keys in `m` to use the camelCase naming convention."
  [m]
  (set/rename-keys
    m
    (reduce
      (fn rename-key [acc k]
        (letfn [(->camel-case [k]
                  (let [key-name (name k)]
                    (keyword (if (str/starts-with? key-name "_")
                               key-name
                               (str/camel-case key-name)))))]
          (cond
            (keyword? k) (assoc acc k (->camel-case k))
            :else acc)))
      {}
      (keys m))))

(defn camelize-keys
  "Recursively rename the keys in `m` to use the camelCase naming convention."
  [m]
  (walk/postwalk
    (fn [v]
      (if (and (map? v)
               (not (record? v)))
        (camelize-keys* v)
        v))
    m))

(defn ->lisp-case [k]
  (let [key-name (name k)]
    (keyword (if (str/starts-with? key-name "_")
               key-name
               (str/lisp-case key-name)))))

(defn- lispy-keys*
  "Rename the keys in `m` to use the lisp-case naming convention."
  [m]
  (set/rename-keys
    m
    (reduce
      (fn rename-key [acc k]
        (cond
          (keyword? k)
          (assoc acc k (->lisp-case k))
          :else acc))
      {}
      (keys m))))

(defn lispy-keys
  "Recurisvely rename the keys in `m` to use the lisp-case naming convention."
  [m]
  (walk/postwalk
    (fn [v]
      (if (and (map? v)
               (not (record? v)))
        (lispy-keys* v)
        v))
    m))

;;;;;;;;;;;;;;;;;;
;; Docker utils ;;
;;;;;;;;;;;;;;;;;;
(defn container-name [{:keys [names]}]
  (some-> (first names)
          (str/replace-first "/" "")))

(defn container-additional-names [{:keys [names]}]
  (some->> (rest names)
           (map #(str/replace-first % "/" ""))))

(defn docker-host-configured? [host]
  (cfg/get :docker :hosts (keyword host)))

;;;;;;;;;;;;;;;
;; Web utils ;;
;;;;;;;;;;;;;;;
(defn json-response [x]
  {:status (if x 200 404)
   :headers {"Content-Type" "text/json"}
   :body (json/write-str
           (if x
             (camelize-keys x)
             {:message "Resource not found"}))})
