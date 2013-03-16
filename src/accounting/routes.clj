(ns accounting.routes
  (:use compojure.core)
  (:use ring.middleware.session)
  (:require [compojure.route :as route])
  (:require [accounting.views :as views]))

(defroutes accounting
  (route/resources "/")
  (GET "/" [] (views/main))
  (GET "/logout" [] (views/logout))
  (GET "/login" [] (views/login))
  (GET "/signup" [] (views/signup))
  (route/not-found (views/page-not-found)))
