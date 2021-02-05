(ns docker-discovery.mqtt.util
  (:require [omniconf.core :as cfg]
            [docker-discovery.docker.host :as host]
            [docker-discovery.util :as util]
            [docker-discovery.docker.container :as container]))

(defmulti device (fn [platform _ _] platform))

(defmulti topic (fn [platform topic-type _ _ _]
                  (-> (str (name platform) "-" (name topic-type))
                      (keyword))))

(defmulti payload (fn [platform topic-type _ _ _ _ _ _ _ _]
                    (-> (str (name platform) "-" (name topic-type))
                        (keyword))))

(defn- ->container-messages [host host-info container*]
  (->> (cfg/get :mqtt :platforms)
       (reduce (fn [entities platform]
                 (let [device* (device platform host host-info)
                       switch-state-topic (topic :home-assistant :state :switch host container*)
                       switch-attributes-topic (topic :home-assistant :attributes :switch host container*)
                       switch-command-topic (topic :home-assistant :command :switch host container*)
                       switch-configuration-topic (topic platform :configuration :switch host container*)]
                   (conj entities
                         {:type :configuration
                          :topic switch-configuration-topic
                          :payload (payload platform :configuration host :switch device* container*
                                            switch-state-topic switch-attributes-topic switch-command-topic {})}
                         {:type :attributes
                          :topic switch-attributes-topic
                          :payload (payload platform :attributes host :switch device* container*
                                            switch-state-topic switch-attributes-topic switch-command-topic {})}
                         {:type :state
                          :topic switch-state-topic
                          :payload (payload platform :state host :switch device* container*
                                            switch-state-topic switch-attributes-topic switch-command-topic
                                            {:help-text "Start/stop Docker container"})})))
               #{})))

(defn- ->host-messages [host*]
  (when-let [host-info (-> (merge (host/info host*)
                                  (host/version host*)
                                  (host/ping host*))
                           (util/trim-to-nil))]
    (some->> (container/find-all host*)
             (mapcat (partial ->container-messages host* host-info)))))

(defn- ->state []
  (some->> (cfg/get :docker :hosts)
           (keys)
           (mapcat ->host-messages)
           (seq)))
