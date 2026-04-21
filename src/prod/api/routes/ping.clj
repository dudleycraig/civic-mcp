(ns api.routes.ping
  (:require
   [clojure.spec.alpha]
   [common.entities.ping]))

(defn ping-handler
  "returns a \"pong\" as a simple health check"
  []
  (fn [_request]
    (let [timestamp (.toString (java.time.Instant/now))
          text      (common.entities.ping/get-pong)
          status    :info
          message   {:message/status status :message/timestamp timestamp}
          messages  {::ping message}]
      {:status 200
       :body {:data text
              :messaging/messages messages}})))

(defn get-routes
  []
  ["/ping"
   {:name ::ping
    :get  {:summary      "health check"
           :description  "returns a simple \"pong\" to verify API is alive"
           :handler      (ping-handler)}}])




