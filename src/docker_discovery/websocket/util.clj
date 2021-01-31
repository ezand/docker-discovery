(ns docker-discovery.websocket.util
  (:require [clojure.set :as set]
            [docker-discovery.docker.host :as host]
            [docker-discovery.docker.container :as container]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg]))

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
