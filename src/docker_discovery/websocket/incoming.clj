(ns docker-discovery.websocket.incoming
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [assoc-in-context dissoc-in-context]]
            [docker-discovery.util :as util]
            [docker-discovery.websocket.util :as ws-util]
            [immutant.web.async :as async]
            [superstring.core :as str]))

(defn- reply! [channel message success? response]
  (async/send! channel (-> (select-keys message [:message-id :command])
                           (util/update-existing-in [:command] (comp str/snake-case name))
                           (assoc :success success?)
                           (merge response)
                           (util/camelize-keys)
                           (json/write-str))))

(defmulti handle-message! :command)
(defmethod handle-message! :default [{:keys [command] :as message} channel]
  (log/trace "Unsupported command received:" command ". Message:" message)
  (reply! channel message true {:message "Unsupported"}))

(defmethod handle-message! :start-listening [message channel]
  (assoc-in-context :websocket [channel :listening?] true)
  (reply! channel message true {:result (ws-util/->state)}))

(defmethod handle-message! :stop-listening [message channel]
  (assoc-in-context :websocket [channel :listening?] false)
  (reply! channel message true {}))
