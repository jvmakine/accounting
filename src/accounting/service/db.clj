(ns accounting.service.db
  (:use [korma db core]))

(defdb accounting-db 
  (postgres 
    {:db "d10hr6kboc1c95"
     :user "nucbyfsiccnhbf"
     :password "wQh-M8f69024IrwEgIGXbm4p9w"
     :host "wQh-M8f69024IrwEgIGXbm4p9w@ec2-54-83-33-14.compute-1.amazonaws.com"
     :port "5432"
     :delimiters ""}))

(defn convert-date [key lst]
  (map #(let [val (key %)]
          (merge % {key (java.util.Date. (.getTime val))}))
       lst))

(defentity users
  (table :users)
  (database accounting-db)
  (pk :id)
  (entity-fields :username :password :id :registration_time :last_login_time))

(defentity account
  (table :account)
  (database accounting-db)
  (pk :id)
  (entity-fields :name :description :id :creation_time)
  (has-one users))

(defentity event
  (table :event)
  (database accounting-db)
  (pk :id)
  (entity-fields :id :account_id :target_account_id :description :amount :cumulative_amount :change_type :event_date)
  (has-one account))
