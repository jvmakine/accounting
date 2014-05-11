(ns accounting.service.db
  (:use [korma db core])
  (:require [environ.core :as env]))

(defdb accounting-db 
  (postgres 
    {:db (env/env :db-name)
     :user (env/env :db-user)
     :password (env/env :db-password)
     :host (env/env :db-host)
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
