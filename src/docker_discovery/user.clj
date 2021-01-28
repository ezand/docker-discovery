(ns docker-discovery.user)

(defonce system (atom {}))

(defn started?
  ([]
   (:started? @system))
  ([service]
   (get-in @system [service :started?])))

(defn set-state
  ([service value]
   (reset! system (assoc-in @system [service :started?] value)))
  ([value]
   (reset! system (assoc @system :started? value))))
