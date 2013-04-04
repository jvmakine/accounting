(ns accounting.routes
  (:use (ring.adapter jetty)
        (ring.middleware file)
        (compojure core)
        (sandbar core auth stateful-session))
  (:require [compojure.route :as route])
  (:require [accounting.views :as views]))

(def my-authenticator 
  {:name "testi" :roles #{}})

(def security-policy
     [#"/"                   [:any :ssl]
      #"/css/.*"              [:any :ssl]
      #"/js/.*"               [:any :ssl]
      #"/login"              [:any :ssl] 
      #"/logout"             [:any :ssl]])	

(defroutes accounting-routes
  (route/resources "/")
  (GET "/" [request] (views/main))
  (GET "/logout" [] (views/logout))
  (GET "/login" [request] (views/login))
  (GET "/signup" [request] (views/signup))
  (route/not-found (views/page-not-found)))

(def accounting 
  (-> accounting-routes
    (with-security security-policy my-authenticator)
    wrap-stateful-session
    ))