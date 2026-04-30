(ns ui.routes.pages
  (:require
   [integrant.core]
   [reagent.core]
   [clojure.string]
   [reitit.frontend.easy]
   [clojure.spec.alpha]
   ["@heroicons/react/24/solid" :as solid-icons-24]
   ["@heroicons/react/20/solid" :as solid-icons-20]
   ["@heroicons/react/16/solid" :as solid-icons-16]
   [datascript.core]
   [common.specs.user]

   [ui.views.pages.login]
   [ui.views.pages.error]
   [ui.views.pages.home]
   [ui.views.pages.about]
   [ui.views.pages.contact]))

(defn get-cookie
  [cookie-name]
  (let [cookies (. js/document -cookie)
        prefix (str cookie-name "=")]
    (when (clojure.string/includes? cookies prefix)
      (let [start (+ (. cookies indexOf prefix) (count prefix))
            end (let [i (. cookies indexOf ";" start)]
                  (if (= i -1) (count cookies) i))]
        (.substring cookies start end)))))

(defn form->map
  [form]
  (let [form-data (js/FormData. form)]
    (reduce
     (fn [accumulator [field-name field-value]]
       (assoc accumulator (keyword field-name) field-value))
     {}
     (js/Array.from form-data))))

(defn coerce
  [spec data]
  (if (clojure.spec.alpha/valid? spec data)
    data
    (throw
     (ex-info
      (str "Validation failed for spec: " spec)
      {:explanation (clojure.spec.alpha/explain-data spec data)}))))

(defn get-csrf
  [{{http :http} :ui}]
  (js/fetch (str (:base-url http) "/csrf")
            (clj->js {:method "GET"
                      :credentials (if (:cors http) "include" "same-origin")})))

(defn post-login-credentials
  [{{http :http} :ui} credentials]
  (let [csrf-token (get-cookie "csrf-token")
        basic-header (->>
                      credentials
                      ((juxt :user/email :user/password))
                      (clojure.string/join ":")
                      (. js/window btoa)
                      (str "Basic "))]
    (js/fetch (str (:base-url http) "/authentication/login")
              (clj->js {:method "POST"
                        :headers {"Authorization" basic-header "X-CSRF-Token" csrf-token}
                        :credentials "include"}))))

(defn login-controller
  [configuration _database]
  (let [state (reagent.core/atom {:status :inert})]
    {:state     state
     :identity  (fn [_] ::login)
     :start     (fn [_]
                  (-> (get-csrf configuration)
                      (.then (fn [response]
                               (if (. response -ok)
                                 (. js/console log "CSRF Initialized")
                                 (. js/console error "CSRF Unavailable"))))
                      (.catch (fn [error]
                                (. js/console error "CSRF Error:" error))))

                  (let [on-change (fn [event]
                                    (let [form (.. event -target -form)]
                                      (swap! state assoc :status (if (. form checkValidity) :dirty :error))))
                        on-submit (fn [event]
                                    (. event preventDefault)
                                    (let [form (. event -target)]
                                      (if (. form checkValidity)
                                        (let [credentials (->> (form->map form) (coerce :user/credentials))]
                                          (swap! state assoc :status :active)
                                          (-> (post-login-credentials configuration credentials)
                                              (.then  (fn [response]
                                                        (if (. response -ok)
                                                          (. response json)
                                                          (throw (js/Error. "Login Failed")))))
                                              (.then  (fn [data]
                                                        (swap! state assoc :status :dirty)
                                                        (. js/console log "Login Successful: " data)
                                                        (reitit.frontend.easy/push-state :ui.routes.pages/home)))
                                              (.catch (fn [error]
                                                        (swap! state assoc :status :error)
                                                        (. js/console error "Login Error: " error)))))
                                        (swap! state assoc :status :error))
                                      (.. event -target -classList (add "validated"))))]
                    (swap! state assoc :on-change on-change :on-submit on-submit)))
     :stop      (fn [_] nil)}))

(defn get-routes [configuration state]
  [["login"
    {:name ::login
     :view ui.views.pages.login/view
     :controllers [(login-controller configuration state)]
     :layout :standalone}]

   ["error"
    {:name ::error
     :view ui.views.pages.error/view
     :label "Error"
     :icon solid-icons-24/ExclamationCircleIcon
     :layout :standalone}]

   [""
    {:name ::home
     :view ui.views.pages.home/view
     :label "Home"
     :icon solid-icons-24/HomeIcon
     :layout :standard}]

   ["about"
    {:name ::about
     :view ui.views.pages.about/view
     :label "About"
     :icon solid-icons-24/IdentificationIcon
     :layout :standard}]

   ["contact"
    {:name ::contact
     :view ui.views.pages.contact/view
     :label "Contact"
     :icon solid-icons-24/AtSymbolIcon
     :layout :standard}]])


