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

(defn date [str] (if (nil? str) nil (utils/string-to-sql-date str)))
(defn min-date [] (java.sql.Date. 0))
(defn max-date [] (utils/string-to-sql-date "9999-12-31"))

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

(defn valid-event-type? [type] 
  (case type
    "change" true
    "transfer" true
    false))

(defn recalculate-cumulatives [events initial]
  (letfn [(comb-help [arg1 arg2] 
                     [(+ (first arg1) (:amount arg2)) 
                      (conj (second arg1) (merge arg2 {:cumulative_amount (+ (first arg1) (:amount arg2))}))])]
    (let [tuple (reduce comb-help [initial []] events)]
      (second tuple))))

(defn current-user [] (session-get "username"))

(defn current-user-id [] (session-get "user-id"))

(defn new-account [name description]
  (account/new name description (current-user-id)))

(defn get-accounts []
  (account/list (current-user-id)))

(defn delete-account [id]
  (account/remove (current-user-id) id))

(defn get-events [account-id from-date to-date]
  (if (account/user-account? (current-user-id) account-id)
    (utils/keys-to-string :event_date 
                          (event/list (current-user-id) 
                                      account-id 
                                      (if (nil? from-date) (min-date) (date from-date)) 
                                      (if (nil? to-date) (max-date) (date to-date))))
    (throw (Throwable. "Illegal account access"))))

(defn get-all-events [from-date to-date]
  (utils/keys-to-string :event_date 
                        (recalculate-cumulatives 
                          (event/get-users-events 
                            (current-user-id)
                            (if (nil? from-date) (min-date) (date from-date))
                            (if (nil? to-date) (max-date) (date to-date))) 0)))

(defn new-event [account-id description amount type event-date]
  (if (account/user-account? (current-user-id) account-id)
    (if (valid-event-type? type)
      (utils/key-to-string :event_date
                            (event/new 
                              account-id description amount type 
                              (date event-date)))
      (throw (Throwable. (str "Illegal event type " type))))
    (throw (Throwable. "Illegal account access"))))

(defn delete-event [account-id event-id]
  (if (account/user-account? (current-user-id) account-id)
    (event/remove account-id event-id)
    (throw (Throwable. "Illegal account access"))))