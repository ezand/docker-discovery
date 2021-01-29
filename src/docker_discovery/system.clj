(ns docker-discovery.system)

(defonce ^:private context* (atom {}))

(defn service-context
  ([service]
   (get-in @context* [:contexts service]))
  ([service context]
   (reset! context* (assoc-in @context* [:contexts service] context))))

(defn remove-service-context [service]
  (reset! context* (update @context* :contexts #(dissoc % service))))

(defn started?
  ([service]
   (service-context service))
  ([]
   (started? :main)))
