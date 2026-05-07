(ns api.routes.administrator
  (:require
   [ring.util.response]
   [buddy.auth]
   [buddy.auth.middleware]))

(defn unauthenticated?
  [request]
  (not (buddy.auth/authenticated? request)))

(defn unauthenticated-handler
  []
  (-> (ring.util.response/response nil)
      (ring.util.response/status 401)))

(defn unauthorized?
  [request role-name]
  (let [user-roles (-> request :identity :user/roles)]
    (not
     (some
      (fn [role]
        (= (name (:role/name role)) (name role-name)))
      user-roles))))

(defn unauthorized-handler
  []
  (-> (ring.util.response/response nil)
      (ring.util.response/status 403)))

(defn wrap-administrator
  [handler]
  (fn [request]
    (cond
      (unauthenticated? request)
      (unauthenticated-handler)

      (unauthorized? request :administrator)
      (unauthorized-handler)

      :else
      (handler request))))

(defn get-routes
  [authentication & child-routes]
  ["/administrator"
   {:middleware
    [[buddy.auth.middleware/wrap-authentication authentication]
     wrap-administrator]}
   (vec child-routes)])




