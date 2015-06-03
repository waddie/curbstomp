(ns curbstomp.core
  (:gen-class)
  (:require [clojure.core.async :refer [<! >! go go-loop chan put! <!! close!]]
            [org.httpkit.client :as http]
            [cheshire.core :as json]))

(def base-url "http://challenge.shopcurbside.com/")

(defn get-session-id "Retrieve a new session ID from the curbside challenge API." []
  (:body @(http/get (str base-url "get-session"))))

(defn -main
  "Traverse curbside challenge API looking for secrets."
  [& args]
  (let [session-ids   (chan)
        request-ids   (chan)
        secret        (atom "")]
    ;; Session IDs are good for 10 requests. Populate a channel with session IDs as they run out.
    (go-loop [new-session-id (get-session-id)]
      (doseq [id (repeat 10 new-session-id)]
        (>! session-ids id))
      (recur (get-session-id)))

    (put! request-ids "start")

    (loop [session-id (<!! session-ids)
           request-id (<!! request-ids)]
      (let [body     (:body @(http/get (str base-url request-id) {:headers {"Session" session-id}}))
            response (json/parse-string body #(keyword (clojure.string/lower-case %)))
            a-secret (:secret response)
            nextval  (:next response)]
        (when (and (string? a-secret) (not= a-secret ""))
          (swap! secret str a-secret))
        (cond
          (nil? nextval)    (close! request-ids)
          (vector? nextval) (doseq [request-id nextval] (put! request-ids request-id))
          :else             (put! request-ids nextval)))
      (let [session-id (<!! session-ids)
            request-id (<!! request-ids)]
        (if-not (nil? request-id)
          (recur session-id request-id)
          (println @secret))))))
