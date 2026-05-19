(ns ui.system.view
  (:require
   [integrant.core]
   [cljs.pprint]
   [datascript.core]
   [reagent.core]
   [ui.utilities]
   [ui.views.components.shell]
   [ui.views.components.main-header]
   [ui.views.components.main-navigation]))

(defn transitional-view
  []
  [:div "CIVIC ZA loading ..."])

(defn public-view
  [_props & children]
  (into [:<>] children))

(defn main-view
  [{match :match routes :routes session :session} & children]
  (let [match-name (-> match :data :name)
        {on-logout :on-logout} (-> match :data :controllers :ui.controllers.logout/controller :state deref)
        {user-email :user/email} ((:read session))
        console? (= match-name :ui.routes.pages/console)]
    [:<>
     [:header {:class (when console? "absolute top-0 left-0 w-full z-50")}
      [ui.views.components.main-header/view {:user-email user-email :on-logout on-logout :match-name match-name}
       [ui.views.components.main-navigation/view {:key "main-menu" :routes routes :session session :match-name match-name}]]]
     [:main.flex-1.relative {:role "main" :class (when console? "overflow-hidden")}
      (if console?
        [:<> children]
        [:section.absolute.inset-0.overflow-y-auto.bg-base-300
         [:article.p-8.min-h-full.mx-auto.max-w-7xl
          [:<> children]]])]
     [:footer.flex-none.bg-base-200.border-t.border-base-300.px-4.py-1.text-xs.flex.justify-between.items-center
      {:role "content info"
       :aria-label "Application Status"
       :class (when console? "absolute bottom-0 left-0 w-full z-50")}
      [:div.flex.gap-4
       [:span "MCP: " [:span.text-success "CONNECTED"]]
       [:span "DATOMIC: " [:span.text-success "ONLINE"]]]
      [:div "© 2026 Civic Za"]]]))

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration router :router session :session}]
  (fn []
    (let [{{data-theme :data-theme} :ui} configuration
          {routes :router/routes match-state :match/state} router
          {{match-view :view match-layout :layout match-name :name} :data :as match} @match-state]
      (if match
        [ui.views.components.shell/view {:data-theme data-theme}
         [main-view {:match match :routes routes :session session} [match-view {:key (name match-name) :match match}]]]
        [transitional-view]))))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)




