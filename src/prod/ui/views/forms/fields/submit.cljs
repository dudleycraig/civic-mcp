(ns ui.views.forms.fields.submit)

(defn field
  [state & children]
  (into
   [:button.btn.btn-primary.w-full
    {:type "submit"
     :disabled (some #(= (:status @state) %) '(:inert :active :error))}]
   (if (= (:status @state) :active)
     [[:span.loading.loading-spinner]]
     children)))
