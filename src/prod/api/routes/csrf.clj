(ns api.routes.csrf
  (:require
   [ring.util.response]))

(defn get-routes
  []
  ["/csrf"
   {:name        ::csrf
    :get         {:summary "retrieve CSRF for login"
                  :handler (fn [_]
                             (let [now (java.time.Instant/now)
                                   body {:messaging/messages {::csrf
                                                              {:message/status :success
                                                               :message/text "Initialization Successful"
                                                               :message/timestamp (.toString now)}}}]
                               (-> (ring.util.response/response body)
                                   (ring.util.response/status 200)
                                   (ring.util.response/header "Cache-Control" "no-store"))))}}])



