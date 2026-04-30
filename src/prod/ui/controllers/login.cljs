(ns ui.controllers.login
  (:require
   [reagent.core]
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

(defn controller
  [configuration _database]
  (let [state (reagent.core/atom {:status :inert})]
    {:state     state
     :identity  (fn [_] :ui.routes.pages/login)
     :start     (fn [_]
                  (-> (ui.api/get-csrf configuration)
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
                                          (-> (ui.api/post-credentials configuration credentials)
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

