(ns accounting.utils
  (:import (java.security MessageDigest)))

(defn md5 [token]
  (let [hash-bytes
        (doto (java.security.MessageDigest/getInstance "MD5")
          (.reset)
          (.update (.getBytes token)))]
    (.toString
      (new java.math.BigInteger 1 (.digest hash-bytes)) 16)))

(defn key-to-string [key obj]
  (let [val (key obj)]
          (merge obj {key (str val)})))

(defn keys-to-string [key lst]
  (map  #(key-to-string key %) lst))

(defn string-to-sql-date [str] 
  (java.sql.Date. (.getTime (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") str))))