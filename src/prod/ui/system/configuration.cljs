(ns ui.system.configuration
  (:require
   [integrant.core]))

;; TODO: implement goog-define via macros
(goog-define http-cors "")
(goog-define http-protocol "")
(goog-define http-host "")
(goog-define http-port "")

(goog-define shadow-active "")
(goog-define shadow-protocol "")
(goog-define shadow-host "")
(goog-define shadow-port "")


(def themes ["light" "dark" "retro" "abyss" "bumblebee" "black" "wireframe" "caramellatte" "coffee" "autumn"])

(defmethod integrant.core/init-key ::service
  [_ {profile :profile}]

  {:ui
   {:data-theme   (get themes 3 "wireframe")
    :http         {:cors      http-cors
                   :protocol  http-protocol
                   :host      http-host
                   :port      http-port
                   :base-url  (str http-protocol "://" http-host ":" http-port)}
    :shadow       {:active    shadow-active
                   :protocol  shadow-protocol
                   :host      shadow-host
                   :port      shadow-port}}})

(defmethod integrant.core/halt-key! ::service
  [_ _]
  nil)

