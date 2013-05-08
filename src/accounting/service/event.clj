(ns accounting.service.event
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn list [user-id account-id] 
  (select db/event
          (fields :description :amount :id)
          (where (= :account_id account-id))))

(defn new [account-id description amount]
  (let [ins (insert db/event 
                   (values {:description description 
                            :account_id account-id
                            :amount amount
                            :change_type "change"
                            :event_date (java.sql.Date. (.getTime (java.util.Date. )))}))]
    {:amount (:amount ins) 
     :description (:description ins) 
     :account_id (:account_id ins)
     :id (:id ins)}))