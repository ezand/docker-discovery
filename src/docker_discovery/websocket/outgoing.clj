(ns docker-discovery.websocket.outgoing
  (:require [docker-discovery.system :refer [service-context]]
            [medley.core :as medley]))

(defn- listening-channels []
  (some->> (service-context :websocket)
           (medley/filter-vals :listening?)
           (keys)))
