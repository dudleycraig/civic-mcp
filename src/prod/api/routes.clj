(ns api.routes
  (:require
   [clojure.tools.logging]
   [clojure.spec.alpha]
   [clojure.string]
   [buddy.auth]
   [buddy.auth.middleware]
   [reitit.ring]
   [reitit.ring.middleware.muuntaja]
   [reitit.ring.coercion]
   [ring.middleware.cors :refer [wrap-cors]]
   [reitit.coercion.spec]
   [reitit.swagger]
   [reitit.swagger-ui]
   [reitit.openapi]
   [ring.util.response]
   [api.routes.csrf]
   [api.routes.authentication]
   [api.routes.administrator]
   [api.routes.ping]
   [api.routes.user]
   [api.routes.resources]))

(defn exception-middleware
  []
  {:name ::exceptions
   :wrap (fn [handler]
           (fn [request]
             (try
               (handler request)
               (catch Exception exception
                 (clojure.tools.logging/error exception "API Error")
                 (-> (ring.util.response/response {:error/message (ex-message exception)})
                     (ring.util.response/status (or (:message/code (ex-data exception)) 500)))))))})

(defn cors-middleware
  [{{http :http shadow :shadow} :api}]
  {:name ::cors
   :wrap (fn [handler]
           (let [cors-handler (wrap-cors
                               handler
                               :access-control-allow-origin       (re-pattern (str (:protocol http) "://" (:host http) ":" (if (:active shadow) (:port shadow) (:port http))))
                               :access-control-allow-methods      [:get :put :post :delete :options]
                               :access-control-allow-credentials  "true"
                               :access-control-allow-headers      #{"x-jnet-api" "x-csrf-token" "x-requested-with" "accept" "accept-encoding" "accept-language" "set-cookie" "authorization" "content-type" "origin"})]
             (fn [request]
               (if (:cors http)
                 (cors-handler request)
                 (handler request)))))})

(defn configuration-middleware
  [configuration]
  {:name ::configuration
   :wrap (fn [handler]
           (fn [request]
             (handler (assoc request :configuration configuration))))})

(defn database-middleware
  [database]
  {:name ::database
   :wrap (fn [handler]
           (fn [request]
             (handler (assoc request :database database))))})

(defn get-routes
  [configuration database authentication]
  [""
   {:middleware
    [(cors-middleware configuration)
     reitit.ring.middleware.muuntaja/format-middleware
     (configuration-middleware configuration)
     (database-middleware database)
     (exception-middleware)
     reitit.ring.coercion/coerce-exceptions-middleware
     reitit.ring.coercion/coerce-request-middleware
     reitit.ring.coercion/coerce-response-middleware]
    :coercion reitit.coercion.spec/coercion
    :swagger {:id ::api}
    :responses {}}

   ["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Civic Pulse API"
                            :description "API for South African demographic and voting analysis"
                            :version "0.0.1"}}
           :handler (reitit.swagger/create-swagger-handler)}}]

   ["/api-docs/*"
    {:get {:no-doc true
           :handler (reitit.swagger-ui/create-swagger-ui-handler
                     {:url "/swagger.json"
                      :config {:validatorUrl nil}})}}]

   (api.routes.csrf/get-routes)

   (api.routes.authentication/get-routes
    authentication)

   (api.routes.ping/get-routes)
   ;; add additional routes here

   (api.routes.administrator/get-routes
    authentication
     ;; add additional administrator routes here
    (api.routes.user/get-routes database))

   (api.routes.resources/get-routes)])




