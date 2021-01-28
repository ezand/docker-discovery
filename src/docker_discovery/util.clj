(ns docker-discovery.util
  (:require [superstring.core :as str]
            [clojure.java.io :as io]
            [omniconf.core :as cfg])
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

(defn file [file-location]
  (when-let [file (io/file file-location)]
    (when (.exists file) file)))

(defn exposure-enabled? [exposure]
  (get (cfg/get :docker-exposure) exposure))
