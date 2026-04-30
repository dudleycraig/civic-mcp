(ns ui.views.pages.login
  (:require
   [reagent.core]
   [clojure.spec.alpha]
   [reitit.frontend.easy]
   [common.specs.user]
   [clojure.string]))

(defn view
  [{state :state}]
  (fn []
    [:section.min-h-screen.flex.items-center.justify-center.bg-base-300
     [:div.card.w-96.bg-base-100.shadow-xl
      [:div.card-body
       [:header.text-center.space-y-2
        [:h1.card-title.justify-center.text-2xl.font-black "CIVIC ZA"]
        [:p.text-sm
         {:class "text-base-content/60"}
         "Please sign in to your account"]]

       [:form.space-y-4.mt-4
        {:name "login"
         :noValidate true
         :on-submit (:on-submit @state)}

          ;; email input
        [:fieldset.form-control
         [:label.label {:for "login-email"}
          [:span.label-text "Email Address"]]
         [:input#login-email.input.input-bordered.peer
          {:name "user/email"
           :type "email"
           :placeholder "user@example.com"
           :required true
           :on-change (:on-change @state)}]

           ;; neutral 
         [:label.label
          {:class "peer-placeholder-shown:peer-[:not(:focus)]:flex peer-invalid:hidden peer-valid:hidden"}
          [:span.label-text-alt.text-neutral "required"]]

           ;; warning
         [:label.label.hidden
          {:class "peer-[:placeholder-shown]:peer-[:focus]:peer-invalid:flex"}
          [:span.label-text-alt.text-warning "required"]]

           ;; invalid
         [:label.label.hidden
          {:class "peer-[:not(:placeholder-shown)]:peer-invalid:flex"}
          [:span.label-text-alt.text-error "invalid"]]

           ;; valid
         [:label.label.hidden
          {:class "peer-valid:flex"}
          [:span.label-text-alt.text-success "valid"]]]

          ;; password input
        [:fieldset.form-control
         [:label.label {:for "login-password"}
          [:span.label-text "Password"]]
         [:input#login-password.input.input-bordered.peer
          {:name "user/password"
           :type "password"
           :placeholder "********"
           :required true
           :min-length "8"
           :on-change (:on-change @state)}]

           ;; neutral 
         [:label.label
          {:class "peer-placeholder-shown:peer-[:not(:focus)]:flex peer-invalid:hidden peer-valid:hidden"}
          [:span.label-text-alt.text-neutral "required"]]

           ;; warning
         [:label.label.hidden
          {:class "peer-[:placeholder-shown]:peer-[:focus]:peer-invalid:flex"}
          [:span.label-text-alt.text-warning "required"]]

           ;; invalid
         [:label.label.hidden
          {:class "peer-[:not(:placeholder-shown)]:peer-invalid:flex"}
          [:span.label-text-alt.text-error "invalid"]]

           ;; valid
         [:label.label.hidden
          {:class "peer-valid:flex"}
          [:span.label-text-alt.text-success "valid"]]]

        [:div.card-actions.justify-end.pt-4
         [:button.btn.btn-primary.w-full
          {:type "submit"
           :disabled (some #(= (:status @state) %) '(:inert :active :error))}
          (if (= (:status @state) :active)
            [:span.loading.loading-spinner]
            "SIGN IN")]]]]]]))


