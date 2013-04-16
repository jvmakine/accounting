(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session))
  (:require [accounting.urls :as urls])
  (:require [accounting.service.user :as user])
  (:require [accounting.service.account :as account])
  (:require [accounting.views :as views])
  (:require [clj-json.core :as json]))

(defn login [username password]
  (if (user/valid-login? username password)
    (do
      (session-put! "username" username)
      (session-put! "roles" #{:user})
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

(defn new-account [name description]
  (account/new name description (current-user)))