(ns api.ping-test
  (:require
   [clojure.test]
   [ring.mock.request]
   [muuntaja.core]
   [api.system-test]))

(clojure.test/use-fixtures :once
  (fn [f]
    (api.system-test/start!)
    (f)
    (api.system-test/stop!)))

(clojure.test/deftest ping-route-test
  (let [router    (api.system-test/get-router)
        request   (-> (ring.mock.request/request :get "/ping")
                      (ring.mock.request/header "Accept" "application/json"))
        response  (router request)
        body      (muuntaja.core/decode "application/json" (:body response))]
    (clojure.test/is (= 200     (:status response)))
    (clojure.test/is (= "info"  (get-in body [:messaging/messages (keyword "api.routes.ping" "ping") :message/status])))
    (clojure.test/is (= "pong"  (:data body)))
    (clojure.test/is (some?     (:messaging/messages body)))))

(comment
  (require 'api.ping-test :reload))




