(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session))
  (:require [accounting.urls :as urls])
  (:require [accounting.utils :as utils])
  (:require [accounting.service.user :as user])
  (:require [accounting.service.account :as account])
  (:require [accounting.service.event :as event])
  (:require [accounting.views :as views]))

(defn login [username password]
  (if (user/valid-login? username password)
    (do
      (session-put! "username" username)
      (session-put! "roles" #{:user})
      (session-put! "user-id" (user/get-user-id username))
      (user/update-login-time username)
      (redirect urls/root))
    (views/login {:login "Invalid username or password"})))

(defn logout []
  (do
    (session-delete-key! "username")
    (session-delete-key!  "roles")
    (redirect urls/login)))

(defn signup [username password password-again]
  (if (= password password-again)
    (if (user/exists? username)
      (views/signup {:username "User already exists"})
      (do
        (user/new username password)
        (redirect urls/root)))
      (views/signup {:password "Passwords do not match"})))

(defn current-user [] (session-get "username"))

(defn current-user-id [] (session-get "user-id"))

(defn new-account [name description]
  (account/new name description (current-user-id)))

(defn get-accounts []
  (account/list (current-user-id)))

(defn delete-account [id]
  (account/remove (current-user-id) id))

(defn get-events [account-id]
  (if (account/user-account? (current-user-id) account-id)
    (utils/keys-to-string :event_date (event/list (current-user-id) account-id))
    (throw (Throwable. "Illegal account access"))))

(defn new-event [account-id description amount]
  (if (account/user-account? (current-user-id) account-id)
    (event/new account-id description amount)
    (throw (Throwable. "Illegal account access"))))