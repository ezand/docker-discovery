(defproject docker-discovery "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [clj-time "0.15.2"]
                 [clojurewerkz/machine_head "1.0.0"]
                 [compojure "1.6.2"]
                 [com.grammarly/omniconf "0.4.2"]
                 [http-kit "2.5.1"]
                 [lispyclouds/clj-docker-client "1.0.2"]
                 [medley "1.3.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.immutant/web "2.1.10"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-basic-authentication "1.1.0"]
                 [superstring "3.0.0"]]
  :main ^:skip-aot docker-discovery.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[clj-kondo "2020.10.10" :exclusions [org.clojure/clojure]]]}
             :uberjar {:aot :all}}
  :aliases {"lint" ["run" "-m" "clj-kondo.main" "--lint" "src"]})
