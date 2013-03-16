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
                          [:table 
                           [:tr
                            [:td (label "username" "Username")]
                            [:td (text-field "username")]]
                           [:tr
                            [:td (label "password" "Password")]
                            [:td (password-field "password")]]
                           [:tr
                            [:td (label "password-again" "Retype password")]
                            [:td (password-field "password-again")]]
                           [:tr
                            [:td (submit-button "Register")]
                            [:td ""]]]
                          )))

(defn login [] (page-template "Login" [:span ""]))

(defn main []
  (page-template "Accounting" 
                 [:p "Testing"]))