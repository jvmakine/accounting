(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session))
  (:require [accounting.urls :as urls]))

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
    (println (str username " signed up!"))
    (redirect urls/root)))