(ns common.regex)


;; 8 to 32 characters,
;; at least one lowercase letter,
;; at least one uppercase letter,
;; at least one digit
;; (def password #"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\da-zA-Z]).{8,32}$")

(def email            #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
(def password         #"^[a-zA-Z0-9]{8,32}$")
(def iso-8601-rfc3339 #"^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.*$")
