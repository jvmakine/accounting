(ns accounting.service.event
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn list [user-id account-id] 
  (select db/event
          (fields :description :amount :id :event_date :cumulative_amount :change_type :account_id)
          (where (= :account_id account-id))
          (order :event_date :ASC)
          (order :id :ASC)))

(defn new [account-id description amount type]
  (let [ins (insert db/event 
                   (values {:description description 
                            :account_id account-id
                            :amount amount
                            :change_type type
                            :event_date (java.sql.Date. (.getTime (java.util.Date. )))}))]
    {:amount (:amount ins) 
     :description (:description ins) 
     :account_id (:account_id ins)
     :cumulative_amount (:cumulative_amount ins)
     :id (:id ins)
     :change_type (:change_type ins)}))

(defn remove [account-id event-id]
  (delete db/event
          (where {:account_id account-id :id event-id})))