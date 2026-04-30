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

(defn standalone-view
  [_props & children]
  (into [:<>] children))

(defn standard-view
  [{router :router} & children]
  [:<>
   [ui.views.components.main-header/view
    [ui.views.components.main-navigation/view {:router router}]]
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
  [_ props]
  (fn []
    (let [{configuration :configuration router :router database :database} props
          route-match @(:match router)
          {{route-view :view route-layout :layout route-name :name} :data} route-match
          route-controllers (:router.match/controllers route-match)
          route-controller (some (fn [route-controller] (when (= ((:identity route-controller)) route-name) route-controller)) route-controllers)
          route-state (:state route-controller)
          {{data-theme :data-theme} :ui} configuration
          props (assoc props :state route-state)]

      [ui.views.components.shell/view {:data-theme data-theme}
       (if (and (= route-name :ui.routes.pages/login) (not route-state))
         [transitional-view]
         (case route-layout
           :standard    [standard-view   props [route-view props]]
           :standalone  [standalone-view props [route-view props]]
           [transitional-view]))])))

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)

