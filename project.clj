(defproject accounting "1.0.0-SNAPSHOT"
  :description "Accounting web application"
  :dependencies [[org.clojure/clojure "1.4.0"],
                 [compojure "1.1.5"]
                 [isaacsu/sandbar "0.4.1"]
                 [hiccup "1.0.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler accounting.routes/accounting}
  )