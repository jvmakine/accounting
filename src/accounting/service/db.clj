(ns accounting.service.db
  (:use [korma db core]))

(defdb accounting-db 
  (postgres 
    {:db "accounting"
     :user "accounting"
     :password "accounting"
     :host "127.0.0.1"
     :port "5432"
     :delimiters ""}))

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
