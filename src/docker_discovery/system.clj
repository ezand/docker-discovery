(ns docker-discovery.system)

(defonce context (atom {}))

(defn started?
  ([]
   (:started? @context))
  ([service]
   (get-in @context [service :started?])))

(defn set-service-state
  ([service value]
   (reset! context (assoc-in @context [service :started?] value)))
  ([value]
   (reset! context (assoc @context :started? value))))
