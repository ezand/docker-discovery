(ns docker-discovery.mqtt.home-assistant.core
  (:require [docker-discovery.mqtt.util :as mqtt-util]
            [docker-discovery.util :as util]
            [superstring.core :as str]))

(defmethod mqtt-util/device :homeassistant
  [_ host {:keys [id version] :as host-info}]
  {:identifiers [id],
   :manufacturer "Docker, Inc.",
   :model "Docker",
   :name (str "Docker - " (name host)),
   :sw_version version})

;;;;;;;;;;;;
;; Topics ;;
;;;;;;;;;;;;
(defmethod mqtt-util/topic :homeassistant-configuration
  [_ _ entity-type host {container-name :name}]
  (->> ["homeassistant"
        (name entity-type)
        (str "docker_" (name host))
        container-name
        "config"]
       (str/join "/")))

(defmethod mqtt-util/topic :homeassistant-state
  [_ _ _ host {id :id container-name :name}]
  (->> ["homeassistant"
        (str "docker_" (name host))
        container-name
        id]
       (str/join "/")))

(defmethod mqtt-util/topic :homeassistant-attributes
  [_ _ _ host {id :id container-name :name}]
  (->> ["homeassistant"
        (str "docker_" (name host))
        container-name
        id
        "attributes"]
       (str/join "/")))

(defmethod mqtt-util/topic :homeassistant-command
  [_ _ _ host {id :id container-name :name}]
  (->> ["homeassistant"
        (str "docker_" (name host))
        container-name
        id
        "set"]
       (str/join "/")))

;;;;;;;;;;;;;;
;; Payloads ;;
;;;;;;;;;;;;;;
(defmethod mqtt-util/payload :homeassistant-configuration
  [_ _ host entity-type device {id :id container-name :name} state-topic attributes-topic command-topic _]
  {:payload_off false
   :payload_on true
   :value_template "{{ value_json.value }}"
   :state_topic state-topic
   :json_attributes_topic attributes-topic
   :command_topic command-topic
   :device device
   :name (str "docker_" (name host) "_" (name container-name) "_" (name entity-type))
   :unique_id (str "dockerdiscovery_" (name host) "_" id)})

(defmethod mqtt-util/payload :homeassistant-attributes
  [_ _ host _ _ {:keys [id image state status command] :as container*} _ _ _ _]
  (-> {:id id
       :image image
       :state state
       :status status
       :docker-host-name (name host)}
      (util/assoc-some :command (util/trim-to-nil command))
      (util/assoc-some :website (util/container-website container*))))

(defmethod mqtt-util/payload :homeassistant-state
  [_ _ host entity-type _ {container-name :name :as container*} _ _ _ {:keys [help-text] :as args}]
  {:genre "user",
   :is_polled false,
   :lastUpdate (System/currentTimeMillis),
   :value (util/container-running? container*),
   :type "bool",
   :value_id (str (name host) "_" (name container-name)),
   :read_only false,
   :label (str (name container-name) ": " (str/capitalize (name entity-type))),
   :write_only false,
   :units "",
   :help help-text})
