(ns common.specs.messaging
  (:require
   [clojure.string]
   [clojure.spec.alpha]
   [spec-tools.core]
   [common.regex]))

;; TODO: move all status definitions into its own static spec
(clojure.spec.alpha/def :message/status
  (spec-tools.core/spec
   {:spec #{:info :inert :active :success :warning :error}
    :description "status of a message"
    :swagger/example :success}))

(clojure.spec.alpha/def :message/code
  (spec-tools.core/spec
   {:spec number?
    :description "http status code of message"
    :swagger/example 200}))

(clojure.spec.alpha/def :message/text
  (spec-tools.core/spec
   {:spec (clojure.spec.alpha/and string? #(not (clojure.string/blank? %)))
    :description "a short description regarding the context of a message"
    :swagger/example "OK"}))

(clojure.spec.alpha/def :message/timestamp
  (spec-tools.core/spec
   {:spec (clojure.spec.alpha/and string? #(re-matches common.regex/iso-8601-rfc3339 %))
    :description "an ISO 8601/RFC3339 formatted string"
    :swagger/example "2026-03-18T11:12:00Z"}))

(clojure.spec.alpha/def :message/data
  (spec-tools.core/spec
   {:spec map?
    :description "additional data defining the message"}))

(clojure.spec.alpha/def :messages/message
  (clojure.spec.alpha/keys :req [:message/status]
                           :opt [:message/data :message/code :message/text :message/timestamp]))

(clojure.spec.alpha/def :messaging/messages
  (spec-tools.core/spec
   {:spec (clojure.spec.alpha/map-of keyword? :messages/message)
    :description "a map of message-id to message"
    :swagger/example {:my-message-id {:message/status :success :message/code 200 :message/text "OK" :message/timestamp "2026-03-18T11:12:00Z" :message/data {}}}}))

(clojure.spec.alpha/def :spec
  (spec-tools.core/spec
   {:spec (clojure.spec.alpha/keys :opt [:messaging/messages])
    :description "standard api response wrapper"
    :swagger/example {:messaging/messages {:my-message-id {:message/status :success :message/code 200 :message/text "OK" :message/timestamp "2026-03-18T11:12:00Z" :message/data {}}}}}))




