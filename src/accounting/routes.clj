(ns accounting.routes
  (:use (ring.adapter jetty)
        (ring.middleware file params)
        (ring.util response)
        (compojure core)
        (sandbar core auth stateful-session))
  (:require [compojure.route :as route])
  (:require [accounting.views :as views])
  (:require [accounting.operations :as operations])
  (:require [accounting.urls :as urls])
  (:require [clj-json.core :as json]))

(defn authenticator [request]
  (if (nil? (session-get "username"))
    (if (= (:uri request) "/index.html")
      (redirect urls/login)
      {:status 401, :headers {}, :body ""})
    {:name (session-get "username") :roles (session-get "roles")}))

(def security-policy
     [#"/index.html"         [:user :ssl]
      #"/"                   [:user :ssl]
      #"/rest/.*"            [:user :ssl]
      #"/css/.*"             [:any :ssl]
      #"/js/.*"              [:any :ssl]
      #"/login"              [:any :ssl] 
      #"/login/post"         [:any :ssl]
      #"/signup"             [:any :ssl]
      #"/signup/post"        [:any :ssl]
      #"/permission-denied"  [:any :ssl]
      #"/logout"             [:any :ssl]])	

(defn wrap-dir-index [handler]
  (fn [req] 
    (handler (update-in req [:uri]
                        #(if (= "/" %) "/index.html" %)))))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn parse-json [body] (json/parse-string (slurp body) true))


(defroutes accounting-routes
  (GET urls/permission-denied [] 
       (views/permission-denied))
  ; Account management
  (GET urls/logout [] 
       (operations/logout))
  (GET urls/login [request] 
       (views/login #{}))
  (POST urls/login [username password] 
        (operations/login username password))
  (GET urls/signup [] 
       (views/signup #{}))
  (POST urls/signup [username password password-again] 
        (operations/signup username password password-again))
  ; RESTful urls for accounts
  (GET urls/account [] 
       (json-response (operations/get-accounts)))
  (POST urls/account {body :body} 
        (json-response (let [json (parse-json body)] (operations/new-account (json :name) (json :description)))))
  (DELETE (str urls/account "/:id") [id] 
       (operations/delete-account (read-string id)))
  ; RESTful urls for events
  (GET (str urls/account "/:id/events") [id from to]
       (json-response (operations/get-events (read-string id) from to)))
  (GET urls/event [from to] 
       (json-response (operations/get-all-events from to)))
  (POST urls/event {body :body} 
        (json-response (let [json (parse-json body)] 
                         (operations/new-event (read-string (json :account_id)) 
                                               (json :description) 
                                               (read-string (json :amount))
                                               (json :change_type)
                                               (json :event_date)
                                               ))))
  (DELETE (str urls/account "/:account_id/events/:event_id") [account_id event_id] 
       (operations/delete-event (read-string account_id) (read-string event_id)))
  ; Other utils
  (route/resources "/")
  (route/not-found (views/page-not-found)))

(def accounting 
  (-> accounting-routes
    (with-security security-policy authenticator)
    wrap-stateful-session
    wrap-params
    wrap-dir-index
    ))

(defn -main [& [port]] (run-jetty #'accounting {:port (read-string port) :join? false}))