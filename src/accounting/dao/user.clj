(ns accounting.dao.user
  (:use [korma db core])
  (:require [accounting.dao.db :as db])
  (:require [accounting.utils :as utils]))

(defentity users
  (table :users)
  (database db/accounting-db)
  (pk :id)
  (entity-fields :username :password :id :registration_time :last_login_time))

(defn new [username password]
  (insert users (values {:username username :password (utils/md5 password)})))

(defn valid-login? [username password]
  (let [user (first (select users (where (= :username username))))]
    (= (utils/md5 password) (:password user))))

(defn update-login-time [username]
  (update users 
          (set-fields {:last_login_time (sqlfn now)})
          (where (= :username username))))