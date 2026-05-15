(ns ui.system.api
  (:require
   [clojure.string]
   [cljs.pprint]
   [reagent.core]
   [integrant.core]
   [reitit.frontend.easy]
   [cljs.reader]
   [ui.utilities]))

(defn post-login
  [{{http :http} :ui} credentials]
  (let [csrf-token (ui.utilities/get-cookie "csrf-token")
        basic-header (->>
                      credentials
                      ((juxt :user/email :user/password))
                      (clojure.string/join ":")
                      (. js/window btoa)
                      (str "Basic "))]
    (js/fetch (str (:base-url http) "/authentication/login")
              (clj->js {:method "POST"
                        :headers {"Authorization" basic-header "X-CSRF-Token" csrf-token}
                        :credentials (if (:cors http) "include" "same-origin")}))))

(defn post-logout
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/authentication/logout")
            (clj->js {:method "POST"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn get-csrf
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/csrf")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn get-session-verify
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/authentication/session/verify")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn get-wards
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/administrator/wards")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration}]
  {:post-login            (partial post-login configuration)
   :post-logout           (partial post-logout configuration)
   :get-csrf              (partial get-csrf configuration)
   :get-session-verify    (partial get-session-verify configuration)
   :get-wards             (partial get-wards configuration)})

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)






