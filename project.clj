(defproject docker-discovery "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [com.grammarly/omniconf "0.4.2"]
                 [medley "1.3.0"]
                 [org.clojure/clojure "1.10.1"]
                 [superstring "3.0.0"]]
  :main ^:skip-aot docker-discovery.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
