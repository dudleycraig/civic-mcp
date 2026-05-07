(ns api.routes.ping
  (:require
   [clojure.spec.alpha]
   [ring.util.response]
   [common.entities.ping]))

(defn ping-handler
  []
  (fn [_request]
    (-> (ring.util.response/response "pong")
        (ring.util.response/status 200))))

(defn get-routes
  []
  ["/ping"
   {:name ::ping
    :get  {:summary      "health check"
           :description  "returns a simple \"pong\" to verify API is alive"
           :handler      (ping-handler)}}])




