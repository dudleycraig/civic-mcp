(ns api.routes
  (:require
   [clojure.tools.logging]
   [clojure.spec.alpha]
   [buddy.auth]
   [buddy.auth.middleware]
   [reitit.ring.middleware.muuntaja]
   [reitit.ring.coercion]
   [reitit.coercion.spec]
   [reitit.swagger]
   [reitit.swagger-ui]
   [reitit.openapi]
   [ring.util.response]
   [api.routes.authentication]
   [api.routes.administrator]
   [api.routes.ping]
   [api.routes.user]))

(defn http-code->status-text
  "given an http response code, returns a status key and message"
  [http-code]
  (let [matrix [[100 200 :active  "Request Processing"]
                [200 300 :success "Request Successful"]
                [300 400 :active  "Request Redirecting"]
                [400 500 :error   "Client Error"]
                [500 600 :error   "System Error"]]]
    (if-let [[_ _ status text] (some (fn [[start end :as row]] (when (<= start http-code (dec end)) row)) matrix)]
      [status text]
      (throw
       (ex-info
        "Invalid HTTP Code"
        {::http-code->status-message
         {:message/status :error
          :message/code 500}})))))

(defn ex-handler
  "handle exceptions generated via
   `(ex-info
      \"Primary Message\"
      {::first-message-id {:message/status :error :message/code 500}
       ::second-message-id {:message/status :error :message/code 500 :message/text \"second message text\"}})`"
  [exception]
  (clojure.tools.logging/error exception "Error")
  (let [timestamp  (.toString (java.time.Instant/now))
        messages   (reduce
                    (fn [accumulator [message-id {data-status :message/status data-code :message/code data-text :message/text data-timestamp :message/timestamp}]]
                      (let [code                     (or data-code 500)
                            [http-status http-text]  (http-code->status-text code)
                            status                   (or data-status http-status)
                            text                     (str (ex-message exception) (or data-text http-text))
                            timestamp                (or data-timestamp timestamp)]
                        (assoc accumulator message-id {:message/status status :message/code code :message/text text :message/timestamp timestamp})))
                    {}
                    (ex-data exception))
        code      (->> (vals messages)
                       (map #(or (:message/code %) 500))
                       (apply max))]
    (-> (ring.util.response/response {:messaging/messages messages})
        (ring.util.response/status code))))

(defn exception-handler
  "handle generic Exceptions"
  [exception]
  (clojure.tools.logging/error exception "Error")
  (let [code          500
        [status text] (http-code->status-text code)
        text          (or (ex-message exception) text)
        timestamp     (.toString (java.time.Instant/now))
        message-id    ::exception-handler
        message       {:message/status status :message/code code :message/text text :message/timestamp timestamp}
        messages      (hash-map message-id message)]
    (-> (ring.util.response/response {:messaging/messages messages})
        (ring.util.response/status code))))

(defn exception-middleware
  []
  {:name ::exceptions
   :wrap (fn [handler]
           (fn [request]
             (try
               (handler request)
               ;; add custom exception handlers here 
               (catch clojure.lang.ExceptionInfo  exception (ex-handler exception))
               (catch Exception                   exception (exception-handler exception)))))})

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
    [reitit.ring.middleware.muuntaja/format-middleware
     (configuration-middleware configuration)
     (database-middleware database)
     (exception-middleware)
     reitit.ring.coercion/coerce-exceptions-middleware
     reitit.ring.coercion/coerce-request-middleware
     reitit.ring.coercion/coerce-response-middleware]

    :coercion
    reitit.coercion.spec/coercion

    :swagger {:id ::api}

    :responses
    {}
    #_{200 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}
       201 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}
       400 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}
       401 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}
       403 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}
       500 {:body (clojure.spec.alpha/keys :opt [:messaging/spec])}}}

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

   (api.routes.authentication/get-routes
    authentication)

   (api.routes.ping/get-routes)
   ;; add additional routes here

   (api.routes.administrator/get-routes
    authentication
     ;; add additional administrator routes here
    (api.routes.user/get-routes database))])




