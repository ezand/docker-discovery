(ns docker-discovery.websocket.util
  (:require [clojure.set :as set]
            [docker-discovery.docker.host :as host]
            [docker-discovery.docker.container :as container]
            [docker-discovery.system :refer [service-context]]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg]
            [medley.core :as medley]))

(defn ->container [{:keys [id name] :as container*}]
  {:id id
   :name name
   :attributes (dissoc container* :id :name)})

(defn ->host [host*]
  (when-let [{:keys [id] :as info} (-> (merge (host/info host*)
                                              (host/version host*)
                                              (host/ping host*))
                                       (util/trim-to-nil))]
    (-> {:id id
         :name host*
         :attributes (set/rename-keys info {:name :internal-name})}
        (util/assoc-some :containers (some->> (container/find-all host*)
                                              (map ->container))))))

(defn ->state
  ([host container-id]
   (when-let [container* (container/find-by-id host container-id)]
     {:state {host (->container container*)}}))
  ([]
   {:state {:hosts (some->> (cfg/get :docker :hosts)
                            (keys)
                            (map ->host)
                            (seq))}}))

(defn- listening-for-object-type-event? [events object-type event]
  (if-some [listening? (get-in events [object-type event])]
    listening?
    true))

(defn listening-channels [object-type {:keys [event]}]
  (some->> (service-context :websocket)
           (medley/filter-vals (fn [{:keys [connected? listening? events]}]
                                 (and connected? listening? (listening-for-object-type-event? events object-type event))))
           (keys)))
