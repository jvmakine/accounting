(ns accounting.service.account
  (:use [korma db core])
  (:require [accounting.service.db :as db]))

(defn new [name description username]
  (insert db/account 
          (values {:name name 
                   :description description 
                   :user (subselect db/users (where {:username username}))})))