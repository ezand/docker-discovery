(ns docker-discovery.mqtt.util
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clojurewerkz.machine-head.client :as client]
            [docker-discovery.docker.container :as container]
            [docker-discovery.docker.host :as host]
            [docker-discovery.log :as log]
            [docker-discovery.system :refer [service-context]]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg]))

(defmulti device (fn [platform _ _] platform))

(defmulti topic (fn [platform topic-type _ _ _]
                  (-> (str (name platform) "-" (name topic-type))
                      (keyword))))

(defmulti payload (fn [platform topic-type _ _ _ _ _ _ _ _]
                    (-> (str (name platform) "-" (name topic-type))
                        (keyword))))

(defn switch-state-topic [platform host container*]
  (topic platform :state :switch host container*))

(defn switch-state-payload [platform host device* container*]
  (payload platform :state host :switch device* container*
           nil nil nil {:help-text "Start/stop Docker container"}))

(defn publish-switch-state-update [platform host container-name container-id state]
  (when-let [mqtt-client (service-context :mqtt)]
    (let [state* (if (= state :start) "running" nil)
          host-info (host/info host)
          device* (device platform host host-info)
          container* {:id container-id :name container-name :state state*}
          topic (switch-state-topic platform host container*)
          payload (switch-state-payload platform host device* container*)]
      (future (client/publish mqtt-client topic (json/write-str payload :escape-slash false) 0 true))
      (log/trace "Published" (name platform) "MQTT state for container" container-id "on host" (name host)))))

(defn- ->container-messages [host host-info container*]
  (->> (cfg/get :mqtt :platforms)
       (reduce (fn [entities platform]
                 (let [device* (device platform host host-info)
                       switch-attributes-topic (topic :homeassistant :attributes :switch host container*)
                       switch-state-topic* (switch-state-topic platform host container*)
                       switch-command-topic (topic :homeassistant :command :switch host container*)
                       switch-configuration-topic (topic platform :configuration :switch host container*)]
                   (conj entities
                         {:type :configuration
                          :topic switch-configuration-topic
                          :payload (payload platform :configuration host :switch device* container*
                                            switch-state-topic* switch-attributes-topic switch-command-topic {})}
                         {:type :attributes
                          :topic switch-attributes-topic
                          :payload (payload platform :attributes host :switch device* container*
                                            switch-state-topic* switch-attributes-topic switch-command-topic {})}
                         {:type :state
                          :topic (switch-state-topic platform host container*)
                          :payload (switch-state-payload platform host device* container*)})))
               #{})))

(defn- ->host-messages [host*]
  (when-let [host-info (-> (merge (host/info host*)
                                  (host/version host*)
                                  (host/ping host*))
                           (util/trim-to-nil))]
    (some->> (container/find-all host*)
             (mapcat (partial ->container-messages host* host-info)))))

(defn ->state []
  (some->> (cfg/get :docker :hosts)
           (keys)
           (mapcat ->host-messages)
           (seq)))

(defn parse-command-topic [topic]
  (let [[platform host-name container-name container-id _] (str/split topic #"/")
        host (some-> (str/split host-name #"_") last keyword)]
    {:platform (keyword platform)
     :host host
     :container-name container-name
     :container-id container-id}))

(defn parse-command-value [^bytes value]
  (when value
    (Boolean/parseBoolean (String. value "UTF-8"))))
