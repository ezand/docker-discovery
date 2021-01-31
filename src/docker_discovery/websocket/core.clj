(ns docker-discovery.websocket.core
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? assoc-in-context dissoc-in-context remove-service-context]]
            [docker-discovery.websocket.incoming :as incoming-handlers]
            [docker-discovery.util :as util]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-websocket]]))

(def ^:private ^:const websocket-path-regex #"^(\/ws$|\/ws\/.*$)")

(defn- coerce-message [message]
  (-> (util/lispy-keys message)
      (util/update-existing-in [:command] (comp util/->lisp-case keyword))))

(defn connect! [channel]
  (log/trace "New WebSocket channel open.")
  (assoc-in-context :websocket [channel :connected?] true)
  (async/send! channel "Ready to reverse your messages!"))

(defn disconnect! [channel {:keys [code reason]}]
  (log/trace "WebSocket channel closed. Code:" code ", Reason:" reason)
  (dissoc-in-context :websocket [channel]))

(defn handle-message! [channel message]
  (-> (json/read-str message :key-fn keyword)
      (coerce-message)
      (incoming-handlers/handle-message! channel)))

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
