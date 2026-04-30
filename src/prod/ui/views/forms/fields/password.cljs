(ns ui.views.forms.fields.password)

(defn field
  [state & children]
  (into
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
    children]))

