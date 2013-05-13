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
        ["select a.name as name, a.id as id, a.description as description, 
(select cumulative_amount from event e where e.account_id = a.id and e.event_date <= current_date order by event_date desc, id desc limit 1) as total 
from account a where a.users_id=? and a.id=? group by a.id" 
         [user-id, account-id]] :results))))    
    
(defn list [user-id]
  (map #(set-default :total 0 %)
  (exec-raw 
      ["select a.name as name, a.id as id, a.description as description, 
(select cumulative_amount from event e where e.account_id = a.id and e.event_date <= current_date order by event_date desc, id desc limit 1) as total 
from account a where a.users_id=? group by a.id order by a.id" 
       [user-id]] :results)))

(defn remove [user-id account-id]
  (do
    (delete db/event 
            (where {:account_id account-id}))
    (delete db/account
            (where {:users_id user-id :id account-id}))))

(defn user-account? [user-id account-id]
  (not (empty? 
    (select db/account
          (where {:users_id user-id :id account-id})))))
  