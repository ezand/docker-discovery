(ns docker-discovery.websocket.outgoing
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [service-context]]
            [docker-discovery.util :as util]
            [immutant.web.async :as async]
            [medley.core :as medley]))

(def ^:const docker-event-types #{:start :stop})

(defn- listening-channels []
  (some->> (service-context :websocket)
           (medley/filter-vals :listening?)
           (keys)))

(defn handle-container-event [host {:keys [status id] :as event}]
  (when (contains? docker-event-types status)
    (log/trace "WebSockets is handling container event:" event)
    (doseq [channel (listening-channels)]
      (async/send! channel (-> {:type :event
                                :event {:source :container
                                        :event status
                                        :host (:id host)
                                        :id id
                                        :timestamp (util/iso-now)}}
                               (util/camelize-keys)
                               (json/write-str))))))
