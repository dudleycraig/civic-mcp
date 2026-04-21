(ns common.entities.user
  (:require
   [clojure.tools.logging]
   [clojure.spec.alpha]
   [clojure.pprint]
   [buddy.hashers]
   [common.entities.utilities]
   [common.specs.user]))

(def transient-admin
  {:user/email      "administrator@test.com"
   :user/first-name "Test"
   :user/last-name  "Administrators"
   :user/password   "password"
   :user/roles      [[:role/name :administrator]]})

(def transient-guest
  {:user/email      "guest@test.com"
   :user/first-name "Test"
   :user/last-name  "Guests"
   :user/password   "password"
   :user/roles      [[:role/name :guest]]})

(defn transient->private
  "transform transient user to private user"
  [{password :user/password roles :user/roles :as transient-user}]
  (-> transient-user
      (dissoc :user/password)
      (assoc :user/uuid (common.entities.utilities/generate-uuid))
      (assoc :user/hash (common.entities.utilities/generate-hash password))
      (assoc :user/roles (or roles []))))

(defn private->public
  "transform private user to public user"
  [private-user]
  (let [public-user (dissoc private-user :user/hash :user/password)]
    (if (clojure.spec.alpha/valid? :user/public public-user)
      public-user
      (throw
       (ex-info
        "Invalid public user"
        {::private->public
         {:message/status :error
          :message/code 500
          :message/data (clojure.spec.alpha/explain-data :user/public public-user)}})))))

(def entities
  [(transient->private transient-admin)
   (transient->private transient-guest)])

(defn add!
  "transforms transient users to private users and writes user to database"
  [transact-worker! & transient-users]
  (when-not (clojure.spec.alpha/valid? (clojure.spec.alpha/coll-of :user/transient :kind sequential?) transient-users)
    (throw
     (ex-info
      "Invalid User Data"
      {::add!
       {:message/status :error
        :message/code 400}})))
  (let [private-users (mapv transient->private transient-users)
        result        (transact-worker! (clojure.spec.alpha/coll-of :user/private :kind vector?) private-users)]
    private-users))

(defn remove!
  [transact-worker! & user-uuids]
  (when-not (clojure.spec.alpha/valid? (clojure.spec.alpha/coll-of :user/uuid :kind sequential?) user-uuids)
    (throw
     (ex-info
      "Invalid User UUID's"
      {::remove!
       {:message/status :error
        :message/code 400}})))
  (->> user-uuids
       (mapv (fn [user-uuid] [:db/retractEntity [:user/uuid user-uuid]]))
       (transact-worker!)))

(defn fetch-all
  [query-worker]
  (query-worker
   '[:find (pull ?e [* {:user/roles [:role/name]}])
     :in $
     :where [?e :user/email]]))

(defn get-user-by-email
  [query-worker email]
  (query-worker
   '[:find (pull ?e [* {:user/roles [:role/name]}])
     :in $ ?email
     :where [?e :user/email ?email]]
   email))




