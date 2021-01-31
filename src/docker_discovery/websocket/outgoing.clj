(ns docker-discovery.websocket.outgoing
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [service-context]]
            [docker-discovery.util :as util]
            [docker-discovery.websocket.util :as ws-util]
            [immutant.web.async :as async]
            [medley.core :as medley]
            [clojure.string :as str]))

(def ^:const docker-event-types #{:create :destroy :rename :start :stop :pause :unpause})

(defn- listening-channels []
  (some->> (service-context :websocket)
           (medley/filter-vals :listening?)
           (keys)))

(defn- ->event [{:keys [status id actor] :as event} host]
  (-> {:source :container
       :event status
       :host (:id host)
       :name (get-in actor [:attributes :name])
       :id id
       :timestamp (util/iso-now)}
      (util/assoc-some :old-name (some-> (get-in actor [:attributes :old-name])
                                         (str/replace-first "/" "")))))

(defn- send-event! [event]
  (log/trace "Sending container event over websocket:" event)
  (doseq [channel (listening-channels)]
    (async/send! channel (-> {:type :event
                              :event event}
                             (util/camelize-keys)
                             (json/write-str)))))

(defmulti handle-container-event :status)

(defmethod handle-container-event :default [event host]
  (send-event! (->event event host)))

(defmethod handle-container-event :create [{:keys [local-name id] :as event} host]
  (-> (dissoc event :local-name)
      (->event host)
      (merge (ws-util/->state local-name id))
      (send-event!)))
