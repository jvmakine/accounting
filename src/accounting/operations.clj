(ns accounting.operations
  (:use [hiccup core page])
  (:use 
    (ring.util response)
    (sandbar core stateful-session)))

(defn login [request]
  (do
    (session-put! "username" "test-user")
    (session-put! "roles" #{:user})
    (redirect "/")))

(defn logout [request]
  (do
    (session-delete-key! "username")
    (session-delete-key!  "roles")
    (redirect "/login")))