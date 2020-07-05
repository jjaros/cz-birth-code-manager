(ns gen-czech-birth-code.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            
            [gen-czech-birth-code.config :as cfg]
            [gen-czech-birth-code.config-utils :as cfg-ut])
  (:gen-class))

(defn get-random-offset-days "Get random offset value used range defined by config/min-required-age and config/max-required-age."
  []
  (+ (cfg-ut/min-rand-offset-days) (rand-int (cfg-ut/max-rand-offset-days))))

(defn sysdate-minus-offset-days "Compute result date as now minus given count of days."
  [offset-days-count]
  (time/minus (time/now) (time/days offset-days-count)))

(defn get-under-age-interval "Get actual age interval corresponding to under-age person."
  []
  (let [now (time/now)]
    (time/interval (time/minus now (time/years cfg/min-adult-age)) now)))

(defn format-date "Provides given date formatted by required format."
  [date format]
  (time-format/unparse (time-format/formatter format) date))

(defn gender-month-addition "Get addition value for month part of czech birth code according to input gender. Possible values are :male and :female"
  [gender]
  (cond (= gender :female) cfg/female-month-addition
        (= gender :male) cfg/male-month-addition
        :else (throw (IllegalArgumentException. "Unknown gender."))))

(defn month-birth-code-addition "Get specific addition value for month part of czech birth code."
  [gender birth-year]
  (if (>= birth-year 2004)
    (+ cfg/after-2004-month-addition (gender-month-addition gender)) 
    (gender-month-addition gender)))

(defn calculate-birt-code-crc "Calculates birth code checksum value."
  [year month day suffix-base]
  (let [crc (mod (Integer/parseInt (str (subs year (- (count year) 2)) month day suffix-base)) cfg/birth-code-crc-modulo)]
    (if (= crc 10) 0 crc)))

(defn calculate-suffix "Calculates suffix for input bitrh code values."
  [year month day suffix-base]
  (if (>= (Integer/parseInt year) cfg/birth-code-suffix-crc-since-year) 
    (str suffix-base (calculate-birt-code-crc year month day suffix-base))
    suffix-base))

(defn to-birth-code-data "Get summarized birth code data. The first one is birth code value computed from birth date. The second one is suffix (including checksum if is required)"
  [gender birth-date]
  (let [year-part (format-date birth-date "yy")
        month-part (format "%02d" (+ (time/month birth-date) (month-birth-code-addition gender (time/year birth-date))))
        day-part (format "%02d" (time/day birth-date))
        rand-suffix-base (format "%03d" (rand-int cfg/birth-code-suffix-max-range))]
    [(str year-part month-part day-part) (str (calculate-suffix (format-date birth-date "yyyy") month-part day-part rand-suffix-base))]))

(defn generate-birth-code "API entrypoint: Provides HTTP response containing JSON with generated data on the birth of person." 
  [req]
  {:status  200
   :headers {"Content-Type" "application/json; charset=UTF-8"}
   :body    (->>
             (let [birth-date (sysdate-minus-offset-days (get-random-offset-days))
                   gender (get cfg/number-gender-mapping (rand-int 2))
                   birth-code-data (to-birth-code-data gender birth-date)]
               (json/write-str {
                                :gender (str/upper-case gender)
                                :is-adult (not (time/within? (get-under-age-interval) birth-date)),
                                :birth-date (str (format-date  birth-date "yyyy-MM-dd")),
                                :birth-code (str (first birth-code-data) (last birth-code-data)),
                                :birth-code-formatted (str (first birth-code-data) "/" (last birth-code-data))
                                })))})

(defroutes app-routes "Application routes/endpoints definitions."
  (context "/v1/b-code" []
    (GET "/generate" [] generate-birth-code)
    ; (POST "/validate" [birth-code] validate-birth-code)
    (route/not-found "Resource not found!"))
  (route/not-found "Resource not found!"))

(defn -main
  "Main App entrypoint."
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3131"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/v1/b-code/"))))