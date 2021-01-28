(ns docker-discovery.config.core
  (:require [docker-discovery.config.env :as env]
            [omniconf.core :as cfg]
            [docker-discovery.util :as util]))

(cfg/define
  {:docker-exposure {:type :edn
                     :default #{:websocket :rest}}
   :docker {:nested {:api-version {:type :string
                                   :default "v1.40"}
                     :hosts {:nested {:name {:uri {:type :string}
                                             :events {:type :boolean}}}}}}
   :websocket {:nested {:refresh {:type :number}}}
   :rest {:nested {:port {:type :number
                          :default 3000}
                   :username {:type :string}
                   :password {:type :string
                              :secret true}}}
   :mqtt {:nested {:broker-uri {:type :string}
                   :username {:type :string}
                   :password {:type :string
                              :secret true}
                   :refresh {:type :number}}}})

(def ^:private ^:const default-config-file-location "/etc/docker-discovery/config.edn")

(defn load-config []
  (when-let [config-file (or (util/file (env/str-value env/CONFIG_FILE))
                             (util/file default-config-file-location))]
    (cfg/populate-from-file config-file))
  (cfg/populate-from-map (env/env->config)))
