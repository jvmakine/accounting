(defproject accounting "1.0.0-SNAPSHOT"
  :description "Accounting web application"
  :dependencies [[org.clojure/clojure "1.4.0"],
                 [compojure "1.1.5"]
                 [isaacsu/sandbar "0.4.1"]
                 [hiccup "1.0.0"]
                 [korma "0.3.0-RC5"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [clj-json "0.5.3"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [ring/ring-devel "1.1.0"]
                 [environ "0.5.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :main accounting.routes/main
  :uberjar-name "accounting-standalone.jar"
  :ring {:handler accounting.routes/accounting}
  :min-lein-version "2.3.4"
  )