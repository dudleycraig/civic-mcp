(ns ui.views.forms.fields.messages)

(defn neutral
  [_props & children]
  [:label.label
   {:class "peer-placeholder-shown:peer-[:not(:focus)]:flex peer-invalid:hidden peer-valid:hidden"}
   (into [:span.label-text-alt.text-neutral] children)])

(defn warning
  [_props & children]
  [:label.label.hidden
   {:class "peer-[:placeholder-shown]:peer-[:focus]:peer-invalid:flex"}
   (into [:span.label-text-alt.text-warning] children)])

(defn error
  [_props & children]
  [:label.label.hidden
   {:class "peer-[:not(:placeholder-shown)]:peer-invalid:flex"}
   (into [:span.label-text-alt.text-error] children)])

(defn success
  [_props & children]
  [:label.label.hidden
   {:class "peer-valid:flex"}
   (into [:span.label-text-alt.text-success] children)])
