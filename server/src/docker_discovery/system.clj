(ns docker-discovery.system
  (:require [docker-discovery.util :as util]))

(defonce ^:private context* (atom {}))

(defn service-context
  ([service]
   (get-in @context* [:contexts service]))
  ([service context]
   (reset! context* (assoc-in @context* [:contexts service] context))))

(defn remove-service-context [service]
  (reset! context* (update @context* :contexts #(dissoc % service))))

(defn conj-context [service x]
  (when-not (service-context service)
    (service-context service #{}))
  (swap! context* update-in [:contexts service] conj x))

(defn disj-context [service x]
  (swap! context* update-in [:contexts service] disj x))

(defn assoc-in-context [service ks v]
  (swap! context* util/assoc-some-in (into [:contexts service] ks) v))

(defn dissoc-in-context [service ks]
  (swap! context* util/dissoc-in (into [:contexts service] ks)))

(defn started?
  ([service]
   (service-context service))
  ([]
   (started? :main)))
