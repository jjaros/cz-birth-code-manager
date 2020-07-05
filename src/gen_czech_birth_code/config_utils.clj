(ns gen-czech-birth-code.config-utils
  (:require [gen-czech-birth-code.config :as cfg])
  (:gen-class))

(defn min-rand-offset-days "Calculates lower limit of the interval used for birth date generator."
  []
  (* cfg/days-in-year cfg/min-required-age))

(defn max-rand-offset-days "Calculates upper limit of the interval used for birth date generator."
  []
  (* (- cfg/max-required-age cfg/min-required-age) cfg/days-in-year))