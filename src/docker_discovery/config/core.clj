(ns docker-discovery.config.core
  (:require [docker-discovery.config.env :as env]
            [docker-discovery.log :as log]
            [docker-discovery.util :as util]
            [omniconf.core :as cfg]))

(cfg/define
  {:log-level {:one-of #{:info :debug :trace :error :warn}
               :default :debug}
   :docker-exposure {:type :edn
                     :default #{:websocket :rest}}
   :docker {:nested {:api-version {:type :string
                                   :default "v1.40"}
                     :hosts {:nested {:name {:uri {:type :string}
                                             :events {:type :boolean}
                                             :username {:type :string}
                                             :password {:type :string
                                                        :secret true}}}}}}
   :http {:nested {:port {:type :number
                          :default 3000}}}
   :websocket {:nested {:refresh {:type :number}}}
   :rest {:nested {:username {:type :string}
                   :password {:type :string
                              :secret true}}}
   :mqtt {:nested {:broker-uri {:type :string}
                   :username {:type :string}
                   :password {:type :string
                              :secret true}
                   :refresh {:type :number}}}})

(def ^:private ^:const default-config-file-location "/etc/docker-discovery/config.edn")

(defn load-config []
  (cfg/set-logging-fn (fn [& args] (log/info args)))
  (when-let [config-file (or (util/file (env/str-value env/CONFIG_FILE))
                             (util/file default-config-file-location))]
    (cfg/populate-from-file config-file))
  (cfg/populate-from-map (env/env->config))
  (cfg/verify))
