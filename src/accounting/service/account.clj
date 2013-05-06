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

(defn set-default [key def m]
  (if (nil? (key m))
    (merge m {key def})
    m))

(defn get [user-id account-id]
  (set-default :total 0
    (first 
      (exec-raw 
        ["select a.name as name, a.id as id, a.description as description, sum(e.amount) as total 
from account a left join event e on (e.account_id = a.id) where a.users_id=? and a.id=? group by a.id" 
         [user-id, account-id]] :results))))    
    
(defn list [user-id]
  (map #(set-default :total 0 %)
  (exec-raw 
      ["select a.name as name, a.id as id, a.description as description, sum(e.amount) as total 
from account a left join event e on (e.account_id = a.id) where a.users_id=? group by a.id" 
       [user-id]] :results)))

(defn remove [user-id account-id]
  (delete db/account
          (where {:users_id user-id :id account-id})))