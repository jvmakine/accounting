(ns accounting.dao.user
  (:use [korma db core])
  (:require [accounting.dao.db :as db]))

(defentity users
  (table :users)
  (database db/accounting-db)
  (pk :id)
  (entity-fields :username :password :id :registration_time))

(defn new [username password_hash]
  (insert users (values {:username username :password password_hash})))

(defn get-by-username [username]
  (first (select users (where (= :username username)))))