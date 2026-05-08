(ns ui.system.view
  (:require
   [integrant.core]
   [cljs.pprint]
   [datascript.core]
   [reagent.core]
   [ui.views.components.shell]
   [ui.views.components.main-header]
   [ui.views.components.main-navigation]))

(defn transitional-view
  []
  [:div "CIVIC ZA loading ..."])

(defn public-view
  [_props & children]
  (into [:<>] children))

(defn private-view
  [{routes :routes match-name :match-name} & children]
  [:<>
   [ui.views.components.main-header/view {:routes routes :match-name match-name}]
   [:main.flex-1.relative
    {:role "main"}
    [:section.absolute.inset-0.overflow-y-auto.bg-base-300
     (into [:article.p-8.min-h-full.mx-auto.max-w-7xl] children)]]
   [:footer.flex-none.bg-base-200.border-t.border-base-300.px-4.py-1.text-xs.flex.justify-between.items-center
    {:role "content info"
     :aria-label "Application Status"}
    [:div.flex.gap-4
     [:span "MCP: " [:span.text-success "CONNECTED"]]
     [:span "DATOMIC: " [:span.text-success "ONLINE"]]]
    [:div "© 2026 Civic Za"]]])

(defmethod integrant.core/init-key ::service
  [_ {configuration :configuration router :router session :session}]
  (fn []
    (let [{{data-theme :data-theme} :ui} configuration
          {routes :router/routes match-state :match/state} router
          {session-state :state} session
          {{match-view :view match-layout :layout match-name :name} :data :as match} @match-state
          session @session-state]

      (if match
        [ui.views.components.shell/view {:data-theme data-theme}
         (case match-layout
           :private [private-view {:match-name match-name :routes routes} [match-view {:match match}]]
           :public  [public-view  {}                                      [match-view {:match match}]]
           [transitional-view])]
        [transitional-view]))))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)
