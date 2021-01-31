(ns docker-discovery.websocket.outgoing
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [service-context]]
            [docker-discovery.util :as util]
            [immutant.web.async :as async]
            [medley.core :as medley]
            [clojure.string :as str]))

(def ^:const docker-event-types #{:start :stop :rename :destroy})

(defn- listening-channels []
  (some->> (service-context :websocket)
           (medley/filter-vals :listening?)
           (keys)))

(defn handle-container-event [host {:keys [status id actor] :as event}]
  (when (contains? docker-event-types status)
    (log/trace "WebSockets is handling container event:" event)
    (doseq [channel (listening-channels)]
      (async/send! channel (-> {:type :event
                                :event (-> {:source :container
                                            :event status
                                            :host (:id host)
                                            :name (get-in actor [:attributes :name])
                                            :id id
                                            :timestamp (util/iso-now)}
                                           (util/assoc-some :old-name (some-> (get-in actor [:attributes :old-name])
                                                                              (str/replace-first "/" ""))))}
                               (util/camelize-keys)
                               (json/write-str))))))
