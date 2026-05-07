(ns ui.controllers.login
  (:require
   [reagent.core]
   [cljs.pprint]
   [clojure.string]
   [reitit.frontend.easy]
   [clojure.spec.alpha]
   [common.specs.user]
   [ui.api]))

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

(defn on-change-handler
  [state event]
  (let [form (.. event -target -form)]
    (swap! state assoc :status (if (. form checkValidity) :dirty :error))))

(defn on-submit-handler
  [configuration session state event]
  (. event preventDefault)
  (let [form (. event -target)]
    (if (. form checkValidity)
      (let [credentials (->> (form->map form) (coerce :user/credentials))]
        (swap! state assoc :status :active)
        (-> (ui.api/post-credentials configuration credentials)
            (.then  (fn [response]
                      (if (. response -ok)
                        (. response json)
                        (throw (js/Error. "Login Failed")))))
            (.then  (fn [json]
                      (swap! state assoc :status :dirty)
                      (let [payload (-> json js->clj ui.api/keywordize)
                            ;; FIX: Use namespaced key for the session save function
                            save-fn (get session :session/save)]
                        (save-fn payload))
                      (reitit.frontend.easy/push-state :ui.routes.pages/home)))
            (.catch (fn [error]
                      (swap! state assoc :status :error)
                      (. js/console error "Login Error: " error)))))
      (swap! state assoc :status :error))
    (.. event -target -classList (add "validated"))))

(defn process-csrf
  [configuration]
  (-> (ui.api/get-csrf configuration)
      (.then (fn [response]
               (if (. response -ok)
                 (. js/console log "CSRF Initialized")
                 (. js/console error "CSRF Unavailable"))))
      (.catch (fn [error]
                (. js/console error "CSRF Error:" error)))))

(defn controller
  [configuration session _domain]
  (let [state (reagent.core/atom {:status :inert})]
    {:name      :ui.controllers.login/login
     :state     state
     :identity  (fn [route-state] route-state)
     :start     (fn [route-state]
                  (swap! state assoc
                         :on-change (partial on-change-handler state)
                         :on-submit (partial on-submit-handler configuration session state))
                  (process-csrf configuration))
     :stop      (fn [route-state] nil)}))
