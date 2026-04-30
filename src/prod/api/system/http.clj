(ns api.system.http
  (:require
   [integrant.core]
   [clojure.tools.logging]
   [ring.adapter.jetty]))

(defn init
  [{{{protocol :protocol host :host port :port} :http} :api} router]
  (println "protocol" protocol "host" host "port" port)
  (when-not (and protocol host port)
    (throw
     (ex-info
      "HTTP Error, Invalid Configuration"
      {::http {:status :error :code 500}})))
  (clojure.tools.logging/info
   "Initializing HTTP Service on " protocol "://" host ":" port " ...")
  (ring.adapter.jetty/run-jetty router {:port port :join? false}))

(defn halt
  [$state]
  (clojure.tools.logging/info "Halting HTTP Service ...")
  (. $state stop))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration
      router :router}]
  (init configuration router))

(defmethod integrant.core/halt-key! ::service
  [_ $state]
  (halt $state))
