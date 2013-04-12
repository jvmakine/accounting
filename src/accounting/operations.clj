(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session)))

(defn login [username password]
  (do
    (session-put! "username" username)
    (session-put! "roles" #{:user})
    (redirect "/")))

(defn logout []
  (do
    (session-delete-key! "username")
    (session-delete-key!  "roles")
    (redirect "/login")))