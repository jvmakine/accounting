(ns accounting.service.user
  (:use [korma db core])
  (:require [accounting.service.db :as db])
  (:require [accounting.utils :as utils]))

(def salt "34p5uedfklsh403")

(defn transform-password [password]
  (utils/md5 (str password salt)))

(defn new [username password]
  (insert db/users (values {:username username :password (transform-password password)})))

(defn valid-login? [username password]
  (let [user (first (select db/users (where (= :username username))))]
    (= (transform-password password) (:password user))))

(defn update-login-time [username]
  (update db/users 
          (set-fields {:last_login_time (sqlfn now)})
          (where (= :username username))))

(defn exists? [username]
  (let [user (first (select db/users (where (= :username username))))]
    (not (nil? user))))

(defn get-user-id [username]
  (:id (first (select db/users (fields :id) (where (= :username username))))))