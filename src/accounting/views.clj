(ns accounting.views
  (:use [hiccup core page form]))

(defn page-template [title contents]
  (html5
    [:head [:title title]
     (include-css "/css/accounting.css")]
    [:body
     [:div {:id "main-area"}
      [:h1 {:id "page-title"} title]
      [:div {:id "content-area"} contents]
     ]]))

(defn page-not-found [] (page-template "Page Not Found!" [:span ""]))

(defn logout [] (page-template "Logged out!" [:span ""]))

(defn signup [] 
  (page-template "Sign up" 
                 (form-to [:put "/signup/post"] 
                          (label "username" "Username")
                          (text-field "username")
                          (label "password" "Password")
                          (text-field "password")
                          )))

(defn login [] (page-template "Login" [:span ""]))

(defn main []
  (page-template "Accounting" 
                 [:p "Testing"]))