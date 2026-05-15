(ns api.authentication-test
  (:require
   [clojure.test]
   [clojure.string]
   [clojure.pprint]
   [clojure.spec.alpha]
   [clojure.java.io]
   [aero.core]
   [datomic.client.api]
   [common.schemas]
   [common.entities]
   [ring.mock.request]
   [muuntaja.core]
   [api.system-test]
   [api.system.database]
   [common.entities.user]))

(def admin-test-transient-user
  {:user/first-name "Test"
   :user/last-name  "Administrators"
   :user/email      "administrator@test.com"
   :user/password   "password"
   :user/roles      [[:role/name :administrator]]})

(def guest-test-transient-user
  {:user/first-name "Test"
   :user/last-name  "Guests"
   :user/email      "guest@test.com"
   :user/password   "password"
   :user/roles      [[:role/name :guest]]})

(clojure.test/use-fixtures :once
  (fn [f]
    (api.system-test/start!)
    (let [{transact-database-entities :transact/entities} (api.system-test/get-database)
          private-users (common.entities.user/add! transact-database-entities admin-test-transient-user guest-test-transient-user)]
      (try
        (f)
        (finally
          (apply common.entities.user/remove! transact-database-entities (map :user/uuid private-users))
          (api.system-test/stop!))))))

(defn decode-json [body]
  (muuntaja.core/decode "application/json" body))

(defn parse-cookie-header [header-string]
  (when header-string
    (reduce (fn [acc cookie]
              (let [[k v] (clojure.string/split cookie #"=" 2)]
                (assoc acc k v)))
            {}
            (clojure.string/split header-string #"; "))))

(defn get-cookie [response name]
  (or (get-in response [:cookies name])
      (let [set-cookie (get-in response [:headers "Set-Cookie"])
            set-cookie (if (coll? set-cookie) (first set-cookie) set-cookie)]
        (when (and set-cookie (clojure.string/includes? set-cookie (str name "=")))
          (let [parts (clojure.string/split set-cookie #";")
                value (second (clojure.string/split (first parts) #"=" 2))
                attrs (reduce (fn [acc part]
                                (let [[k v] (clojure.string/split part #"=" 2)]
                                  (assoc acc (clojure.string/lower-case (or k "")) v)))
                              {}
                              (rest parts))]
            {:value value
             :max-age (when-let [ma (get attrs "max-age")] (Integer/parseInt ma))})))))

(clojure.test/deftest login-test
  (let [handler     (api.system-test/get-handler)
        email       (:user/email admin-test-transient-user)
        password    (:user/password admin-test-transient-user)
        auth-string (str email ":" password)
        auth-header (str "Basic " (.encodeToString (java.util.Base64/getEncoder) (.getBytes auth-string)))
        request     (-> (ring.mock.request/request :get "/authentication/login")
                        (ring.mock.request/header "authorization" auth-header))
        response    (handler request)
        body        (decode-json (:body response))]

    (clojure.test/testing "Login with correct credentials"
      (clojure.test/is (= 200 (:status response)))
      (clojure.test/is (= "Login Successful" (get-in body [:messaging/messages (keyword "api.routes.authentication" "login-success-handler") :message/text])))
      (clojure.test/is (some? (get-cookie response "token"))))

    (clojure.test/testing "Login with incorrect credentials"
      (let [bad-request (-> (ring.mock.request/request :get "/authentication/login")
                            (ring.mock.request/header "authorization" "Basic d3Jvbmc6cGFzc3dvcmQ=")) ;; wrong:password
            bad-response (handler bad-request)]
        (clojure.test/is (= 401 (:status bad-response)))))))

(clojure.test/deftest logout-test
  (let [handler  (api.system-test/get-handler)
        request  (ring.mock.request/request :get "/authentication/logout")
        response (handler request)
        body     (decode-json (:body response))]

    (clojure.test/testing "Logout clears the session cookie"
      (clojure.test/is (= 200 (:status response)))
      (clojure.test/is (= "Logout Successful" (get-in body [:messaging/messages (keyword "api.routes.authentication" "get-routes") :message/text])))
      (clojure.test/is (= -1 (:max-age (get-cookie response "token")))))))

(clojure.test/deftest authorization-test
  (let [handler     (api.system-test/get-handler)
        email       (:user/email admin-test-transient-user)
        password    (:user/password admin-test-transient-user)
        auth-string (str email ":" password)
        auth-header (str "Basic " (.encodeToString (java.util.Base64/getEncoder) (.getBytes auth-string)))
        ;; Login to get token
        login-resp  (handler (-> (ring.mock.request/request :get "/authentication/login")
                                 (ring.mock.request/header "authorization" auth-header)))
        token       (:value (get-cookie login-resp "token"))]

    (clojure.test/testing "Access restricted administrator route with token"
      (let [request  (-> (ring.mock.request/request :post "/administrator/user/add")
                         (ring.mock.request/header "cookie" (str "token=" token))
                         (ring.mock.request/json-body {:user/first-name "New" :user/last-name "User" :user/email "new@test.com" :user/password "password"}))
            response (handler request)]
        (clojure.test/is (= 201 (:status response)))))))

(comment
  (do
    (require 'api.system-test :reload)
    (require 'api.authentication-test :reload)))
