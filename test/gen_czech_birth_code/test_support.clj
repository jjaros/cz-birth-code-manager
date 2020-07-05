(ns gen-czech-birth-code.test-support
  (:require 
   [clj-time.format :as time-format]
   [gen-czech-birth-code.config :as cfg])
  (:gen-class))

(defn min-rand-offset-days 
  "Compute count of days from today to low range of interval for birth code generator. Constant '365' will be used as count of days in one year." 
  []
  (* 365 cfg/min-required-age))

(defn max-rand-offset-days 
  "Compute count of days from today to high range of interval for birth code generator. Constant '365 'will be used as count of days in one year."
  []
  (* (- cfg/max-required-age cfg/min-required-age) 365))

(defn date-only 
  "Parse only date part from given input date (format 'yyyy-MM-dd' will be used)."
  [date]
  (time-format/unparse (time-format/formatter "yyyy-MM-dd") date))