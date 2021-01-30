(ns docker-discovery.websocket.core
  (:require [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? disj-context conj-context service-context remove-service-context]]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-websocket]]
            [docker-discovery.util :as util]))

(def ^:private ^:const websocket-path-regex #"^(\/ws$|\/ws\/.*$)")

(defn connect! [channel]
  (log/trace "New WebSocket channel open.")
  (conj-context :websocket channel)
  (async/send! channel "Ready to reverse your messages!"))

(defn disconnect! [channel {:keys [code reason]}]
  (log/trace "WebSocket channel closed. Code:" code ", Reason:" reason)
  (disj-context :websocket channel))

(defn handle-message! [channel message]
  (async/send! channel (apply str (reverse message))))

(defn handle-error [channel ex]
  (log/error "A WebSocket error occurred" ex))

(def websocket-callbacks
  "WebSocket callback functions."
  {:on-open connect!
   :on-close disconnect!
   :on-message handle-message!
   :on-error handle-error})

(defn wrap-ws [handler]
  (fn [{:keys [uri] :as request}]
    (if (and (util/exposure-enabled? :websocket)
             (re-matches websocket-path-regex uri))
      (let [websocket-handler (wrap-websocket handler websocket-callbacks)]
        (log/trace "Handling WebSocket request:" uri)
        (websocket-handler request))
      (handler request))))

(defn stop []
  (when (started? :websocket)
    (remove-service-context :websocket)
    (log/info "Websocket service has stopped.")))
