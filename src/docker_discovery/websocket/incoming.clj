(ns docker-discovery.websocket.incoming
  (:require [clojure.data.json :as json]
            [docker-discovery.docker.host :as host]
            [docker-discovery.docker.container :as container]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [assoc-in-context dissoc-in-context]]
            [immutant.web.async :as async]
            [docker-discovery.util :as util]
            [superstring.core :as str]
            [omniconf.core :as cfg]
            [clojure.set :as set]))

(defn- reply! [channel message success? response]
  (async/send! channel (-> (select-keys message [:message-id :command])
                           (util/update-existing-in [:command] (comp str/snake-case name))
                           (assoc :success success?)
                           (merge response)
                           (util/camelize-keys)
                           (json/write-str))))

(defn- ->container [{:keys [id name] :as container*}]
  {:id id
   :name name
   :attributes (dissoc container* :id :name)})

(defn- ->host [host*]
  (when-let [{:keys [id] :as info} (-> (merge (host/info host*)
                                                   (host/version host*)
                                                   (host/ping host*))
                                            (util/trim-to-nil))]
    (-> {:id id
         :name host*
         :attributes (set/rename-keys info {:name :internal-name})}
        (util/assoc-some :containers (some->> (container/find-all host*)
                                              (map ->container))))))

(defn ->state []
  {:state {:hosts (some->> (cfg/get :docker :hosts)
                           (keys)
                           (map ->host)
                           (seq))}})

(defmulti handle-message! :command)
(defmethod handle-message! :default [{:keys [command] :as message} channel]
  (log/trace "Unsupported command received:" command ". Message:" message)
  (reply! channel message true {:message "Unsupported"}))

(defmethod handle-message! :start-listening [message channel]
  (assoc-in-context :websocket [channel :listening?] true)
  (reply! channel message true {:result (->state)}))

(defmethod handle-message! :stop-listening [message channel]
  (assoc-in-context :websocket [channel :listening?] false)
  (reply! channel message true {}))
