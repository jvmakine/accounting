(ns accounting.dao.db
  (:use [korma db core]))

(defdb accounting-db 
  (postgres 
    {:db "accounting"
     :user "accounting"
     :password "accounting"
     :host "127.0.0.1"
     :port "5432"
     :delimiters ""}))