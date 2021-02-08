(ns docker-discovery.websocket.core
  (:require [clojure.data.json :as json]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [started? service-context assoc-in-context
                                             dissoc-in-context remove-service-context]]
            [docker-discovery.websocket.incoming :as incoming-handlers]
            [docker-discovery.util :as util]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-websocket]]
            [omniconf.core :as cfg]
            [docker-discovery.websocket.util :as ws-util])
  (:import [java.util.concurrent TimeUnit]))

(def ^:private ^:const websocket-path-regex #"^(\/ws$|\/ws\/.*$)")

(defn- coerce-message [message]
  (-> (util/lispy-keys message)
      (util/update-existing-in [:command] (comp util/->lisp-case keyword))))

(defn connect! [channel]
  (log/trace "New WebSocket channel open.")
  (assoc-in-context :websocket [channel :connected?] true)
  (async/send! channel (json/write-str {:message "Connected!"})))

(defn disconnect! [channel {:keys [code reason]}]
  (log/trace "WebSocket channel closed. Code:" code ", Reason:" reason)
  (dissoc-in-context :websocket [channel]))

(defn handle-message! [channel message]
  (-> (json/read-str message :key-fn keyword)
      (coerce-message)
      (incoming-handlers/handle-message! channel)))

(defn handle-error [_ ex]
  (log/error "A WebSocket error occurred" ex))

(def websocket-callbacks
  "WebSocket callback functions."
  {:on-open connect!
   :on-close disconnect!
   :on-message handle-message!
   :on-error handle-error})

(defn wrap-ws
  "Ring handler to wrap a websocket request."
  [handler]
  (fn [{:keys [uri] :as request}]
    (if (and (util/exposure-enabled? :websocket)
             (re-matches websocket-path-regex uri))
      (let [websocket-handler (wrap-websocket handler websocket-callbacks)]
        (log/trace "Handling WebSocket request:" uri)
        (websocket-handler request))
      (handler request))))

(defn- refresh-state! []
  (let [state {:type :event
               :event :state-refresh
               :result (ws-util/->state)}]
    (doseq [channel (ws-util/listening-channels :state :state-refresh)]
      (async/send! channel (-> state
                               (util/camelize-keys)
                               (json/write-str)))
      (log/trace "State refreshed and sent to listening websocket clients"))))

(defn start []
  (when-not (started? :websocket)
    (when-let [refresh-interval-seconds (cfg/get :websocket :refresh)]
      (->> (util/set-interval refresh-state! (.toMillis (TimeUnit/SECONDS) refresh-interval-seconds))
           (assoc-in-context :refresh-jobs [:websocket]))
      (log/info "Scheduled a state refresh every" refresh-interval-seconds "second(s) to be sent to listening websocket clients"))
    (log/info "Websocket service is started.")))

(defn stop []
  (when (started? :websocket)
    (some-> (service-context :refresh-jobs)
            :websocket
            (future-cancel))
    (dissoc-in-context :refresh-jobs [:websocket])
    (remove-service-context :websocket)
    (log/info "Websocket service has stopped.")))
