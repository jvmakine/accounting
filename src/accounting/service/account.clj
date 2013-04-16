(ns accounting.service.account
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn new [name description user-id]
  (insert db/account 
          (values {:name name 
                   :description description 
                   :users_id user-id})))

(defn list [user-id]
  (select db/account
          (fields :name :id :description)
          (where {:users_id user-id})))