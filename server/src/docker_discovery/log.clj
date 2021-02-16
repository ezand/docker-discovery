(ns docker-discovery.log
  (:require [clojure.pprint :as pprint])
  (:import [ch.qos.logback.classic Level Logger]
           [java.io StringWriter]
           [org.slf4j LoggerFactory MDC]))

(def logger ^Logger (LoggerFactory/getLogger "docker-discovery"))

(defn set-log-level!
  "Pass keyword :error :info :debug :warn :trace"
  [level]
  (case level
    :debug (.setLevel logger Level/DEBUG)
    :trace (.setLevel logger Level/TRACE)
    :info (.setLevel logger Level/INFO)
    :error (.setLevel logger Level/ERROR)
    :warn (.setLevel logger Level/WARN)))

(defmacro with-logging-context
  "Use this to add a map to any logging wrapped in the macro. Macro can be nested.
  (with-logging-context {:key \"value\"} (log/info \"yay\"))"
  [context & body]
  `(let [wrapped-context# ~context
         ctx# (MDC/getCopyOfContextMap)]
     (try
       (if (map? wrapped-context#)
         (doall (map (fn [[k# v#]] (MDC/put (name k#) (str v#))) wrapped-context#)))
       ~@body
       (finally
         (if ctx#
           (MDC/setContextMap ctx#)
           (MDC/clear))))))

(defmacro trace [& msg]
  `(.trace logger (print-str ~@msg)))

(defmacro debug [& msg]
  `(.debug logger (print-str ~@msg)))

(defmacro info [& msg]
  `(.info logger (print-str ~@msg)))

(defmacro error [throwable & msg]
  `(if (instance? Throwable ~throwable)
     (.error logger (print-str ~@msg) ~throwable)
     (.error logger (print-str ~throwable ~@msg))))

(defmacro warn [throwable & msg]
  `(if (instance? Throwable ~throwable)
     (.warn logger (print-str ~@msg) ~throwable)
     (.warn logger (print-str ~throwable ~@msg))))

(defmacro spy
  [expr]
  `(let [a# ~expr
         w# (StringWriter.)]
     (pprint/pprint '~expr w#)
     (.append w# " => ")
     (pprint/pprint a# w#)
     (error (.toString w#))
     a#))
