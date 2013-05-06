(ns accounting.service.event
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn list [user-id account-id] 
  (select db/event
          (fields :description :amount :id)
          (where (= :account_id account-id))))