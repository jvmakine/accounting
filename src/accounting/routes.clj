(ns accounting.routes
  (:use (ring.adapter jetty)
        (ring.middleware file params)
        (ring.util response)
        (compojure core)
        (sandbar core auth stateful-session))
  (:require [compojure.route :as route])
  (:require [accounting.views :as views])
  (:require [accounting.operations :as operations])
  (:require [accounting.urls :as urls]))

(defn authenticator [request]
  (if (nil? (session-get "username")) 
    (redirect urls/login)
    {:name (session-get "username") :roles (session-get "roles")}))

(def security-policy
     [#"/index.html"         [:user :ssl]
      #"/"                   [:user :ssl]
      #"/api/*"              [:user :ssl]
      #"/css/.*"             [:any :ssl]
      #"/js/.*"              [:any :ssl]
      #"/login"              [:any :ssl] 
      #"/login/post"         [:any :ssl]
      #"/signup"             [:any :ssl]
      #"/signup/post"        [:any :ssl]
      #"/logout"             [:any :ssl]])	

(defn wrap-dir-index [handler]
  (fn [req] 
    (handler (update-in req [:uri]
                        #(if (= "/" %) "/index.html" %)))))

(defroutes accounting-routes
  (GET urls/logout [] (operations/logout))
  (GET urls/login [request] (views/login #{}))
  (POST urls/login [username password] (operations/login username password))
  (GET urls/signup [] (views/signup #{}))
  (POST urls/signup [username password password-again] (operations/signup username password password-again))
  (POST urls/new-account [name description] (operations/new-account name description))
  (route/resources "/")
  (route/not-found (views/page-not-found)))

(def accounting 
  (-> accounting-routes
    (with-security security-policy authenticator)
    wrap-stateful-session
    wrap-params
    wrap-dir-index
    ))