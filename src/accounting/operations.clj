(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session))
  (:require [accounting.urls :as urls])
  (:require [accounting.dao.user :as user])
  (:require [accounting.utils :as utils]))

(defn login [username password]
  (let [user (user/get-by-username username)]
    (if (= (utils/md5 password) (:password user))
      (do
        (session-put! "username" username)
        (session-put! "roles" #{:user})
        (redirect urls/root))
      (redirect urls/login))))

(defn logout []
  (do
    (session-delete-key! "username")
    (session-delete-key!  "roles")
    (redirect urls/login)))

(defn signup [username password password-again]
  (if (= password password-again)
    (do
      (user/new username (utils/md5 password))
      (redirect urls/root))
    (redirect urls/signup)))