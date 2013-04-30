(ns accounting.service.account
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn new [name description user-id]
  (let [id (insert db/account 
                   (values {:name name 
                            :description description 
                            :users_id user-id}))]
    {:name (:name id) 
     :description (:description id) 
     :id (:id id)}))

(defn get [user-id account-id]
  (first (select db/account
                 (fields :name :id :description)
                 (where {:users_id user-id :id account-id}))))    
    
(defn list [user-id]
  (select db/account
          (fields :name :id :description)
          (where {:users_id user-id})))