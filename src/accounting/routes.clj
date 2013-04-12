(ns accounting.routes
  (:use (ring.adapter jetty)
        (ring.middleware file params)
        (ring.util response)
        (compojure core)
        (sandbar core auth stateful-session))
  (:require [compojure.route :as route])
  (:require [accounting.views :as views])
  (:require [accounting.operations :as operations]))

(defn authenticator [request]
  (if (nil? (session-get "username")) 
    (redirect "/login")
    {:name (session-get "username") :roles (session-get "roles")}))

(def security-policy
     [#"/"                   [:user :ssl]
      #"/css/.*"             [:any :ssl]
      #"/js/.*"              [:any :ssl]
      #"/login"              [:any :ssl] 
      #"/login/post"         [:any :ssl]
      #"/signup"             [:any :ssl]
      #"/signup/post"        [:any :ssl]
      #"/logout"             [:any :ssl]])	

(defroutes accounting-routes
  (route/resources "/")
  (GET "/" [request] (views/main))
  (GET "/logout" [] (operations/logout))
  (GET "/login" [request] (views/login))
  (POST "/login/post" [username password] (operations/login username password))
  (GET "/signup" [request] (views/signup))
  (route/not-found (views/page-not-found)))

(def accounting 
  (-> accounting-routes
    (with-security security-policy authenticator)
    wrap-stateful-session
    wrap-params
    ))