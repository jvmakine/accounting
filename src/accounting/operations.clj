(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session))
  (:require [accounting.urls :as urls])
  (:require [accounting.dao.user :as user])
  (:import (java.security MessageDigest)))

(defn md5 [token]
  (let [hash-bytes
        (doto (java.security.MessageDigest/getInstance "MD5")
          (.reset)
          (.update (.getBytes token)))]
    (.toString
      (new java.math.BigInteger 1 (.digest hash-bytes))
      16)))

(defn login [username password]
  (do
    (session-put! "username" username)
    (session-put! "roles" #{:user})
    (redirect urls/root)))

(defn logout []
  (do
    (session-delete-key! "username")
    (session-delete-key!  "roles")
    (redirect urls/login)))

(defn signup [username password]
  (do
    (user/new-user username (md5 password))
    (redirect urls/root)))