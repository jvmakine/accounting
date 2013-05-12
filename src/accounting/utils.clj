(ns accounting.utils
  (:import (java.security MessageDigest)))

(defn md5 [token]
  (let [hash-bytes
        (doto (java.security.MessageDigest/getInstance "MD5")
          (.reset)
          (.update (.getBytes token)))]
    (.toString
      (new java.math.BigInteger 1 (.digest hash-bytes)) 16)))

(defn keys-to-string [key lst]
  (map #(let [val (key %)]
          (merge % {key (str val)})) 
       lst))