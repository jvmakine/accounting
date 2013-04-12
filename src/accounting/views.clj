(ns accounting.views
  (:use [hiccup core page form]))

(defn page-template [title contents]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :session {}
   :body
   (html5
     [:head [:title title]
      (include-css "/css/accounting.css")
      (include-css "/css/cupertino/jquery-ui-1.10.2.custom.min.css")
      (include-js "/js/jquery-1.9.1.js")
      (include-js "/js/jquery-ui-1.10.2.custom.min.js")
      (include-js "/js/decorator.js")]
     [:body
      [:div {:id "main-area"}
       [:h1 {:id "page-title"} title]
       [:div {:id "content-area"} contents]
       ]])})

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

(defn login [] 
  (page-template "Login" 
                 (form-to [:put "/login/post"]
                          [:table
                           [:tr
                            [:td (label "username" "Username")]
                            [:td (text-field "username")]]
                           [:tr
                            [:td (label "password" "Password")]
                            [:td (password-field "password")]]
                           [:tr
                            [:td (submit-button "Login")]
                            [:td ""]]
                           ]
                          [:div {:class "footer"}
                           [:a {:href "/signup"} "Sign up"]])))

(defn main []
  (page-template "Accounting" 
                 [:p "Testing"]))