(ns accounting.views
  (:use [hiccup core page form])
  (:use (sandbar core stateful-session)
        (ring.util response))
  (:require [accounting.urls :as urls]))

(defn page-template [title contents]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session {}
   :body
   (html5
     [:head [:title title]
      (include-css "/css/accounting.css")
      (include-css "/css/cupertino/jquery-ui-1.10.2.custom.min.css")
      (include-js "/js/lib/jquery-1.9.1.js")
      (include-js "/js/lib/jquery-ui-1.10.2.custom.min.js")
      (include-js "/js/decorator.js")]
     [:body
      [:div {:id "main-area"}
       [:h1 {:id "page-title"} title]
       [:div {:id "content-area"} contents]
       ]])})

(defn page-not-found [] (page-template "Page Not Found!" [:span ""]))

(defn print-error [key errors]
  (let [msg (key errors)]
    (if (nil? msg) "" [:div {:class "error"} msg])))

(defn jqueryui-field []
  {:class "text ui-widget-content ui-corner-all"})

(defn signup [errors] 
  (page-template "Sign up" 
                 (form-to [:post urls/signup]
                          [:table 
                           [:tr
                            [:td (label "username" "Username")]
                            [:td (text-field (jqueryui-field) "username")]
                            [:td (print-error :username errors)]]
                           [:tr
                            [:td (label "password" "Password")]
                            [:td (password-field (jqueryui-field) "password")]
                            [:td (print-error :password errors)]]
                           [:tr
                            [:td (label "password-again" "Retype password")]
                            [:td (password-field (jqueryui-field) "password-again")]
                            [:td (print-error :password errors)]]
                           [:tr
                            [:td (submit-button "Register")]
                            [:td ""]
                            [:td ""]]]
                          )))

(defn login [errors] 
  (page-template "Login" 
                 (form-to [:post urls/login]
                          [:table
                           [:tr
                            [:td (label "username" "Username")]
                            [:td (text-field (jqueryui-field) "username")]
                            [:td (print-error :login errors)]]
                           [:tr
                            [:td (label "password" "Password")]
                            [:td (password-field (jqueryui-field) "password")]
                            [:td (print-error :login errors)]]
                           [:tr
                            [:td (submit-button "Login")]
                            [:td ""]
                            [:td ""]]
                           ]
                          [:div {:class "footer"}
                           [:a {:href urls/signup} "Sign up"]])))