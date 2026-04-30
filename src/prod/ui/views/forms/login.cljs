(ns ui.views.forms.login
  (:require
   [ui.views.forms.fields]
   [ui.views.forms.fields.email]
   [ui.views.forms.fields.password]
   [ui.views.forms.fields.submit]
   [ui.views.forms.fields.messages]))

(defn form
  [{state :state}]
  [:form.space-y-4.mt-4
   {:name "login"
    :noValidate true
    :on-submit (:on-submit @state)}

   [ui.views.forms.fields.email/field state
    [ui.views.forms.fields.messages/neutral {:key "neutral"} "neutral"]
    [ui.views.forms.fields.messages/warning {:key "warning"} "warning"]
    [ui.views.forms.fields.messages/error   {:key "error"}   "error"]
    [ui.views.forms.fields.messages/success {:key "success"} "success"]]

   [ui.views.forms.fields.password/field state
    [ui.views.forms.fields.messages/neutral {:key "neutral"} "neutral"]
    [ui.views.forms.fields.messages/warning {:key "warning"} "warning"]
    [ui.views.forms.fields.messages/error   {:key "error"}   "error"]
    [ui.views.forms.fields.messages/success {:key "success"} "success"]]

   [:div.card-actions.justify-end.pt-4
    [ui.views.forms.fields.submit/field state
     "submit"]]])



