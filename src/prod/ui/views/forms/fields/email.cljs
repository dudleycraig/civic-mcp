(ns ui.views.forms.fields.email)

(defn field
  [state & children]
  (into
   [:fieldset.form-control
    [:label.label {:for "login-email"}
     [:span.label-text "Email Address"]]
    [:input#login-email.input.input-bordered.peer
     {:name "user/email"
      :type "email"
      :placeholder "user@example.com"
      :required true
      :on-change (:on-change @state)}]
    children]))

