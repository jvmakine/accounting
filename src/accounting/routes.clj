(ns accounting.routes
  (:use compojure.core)
  (:use ring.middleware.session)
  (:require [compojure.route :as route])
  (:require [accounting.views :as views]))

(defroutes accounting-routes
  (route/resources "/")
  (GET "/" [request] (views/main))
  (GET "/logout" [] (views/logout))
  (GET "/login" [request] (views/login))
  (GET "/signup" [request] (views/signup))
  (route/not-found (views/page-not-found)))

(def accounting (-> 
                  accounting-routes
                  wrap-session))