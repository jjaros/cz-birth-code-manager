(ns gen-czech-birth-code.config
  (:gen-class))

;; year days count (the leap year is not considered)
(def ^:const days-in-year 365)

;; min edge of interval (in years) for generated birth date
(def ^:const min-required-age 1)
;; max edge of interval (in years) for generated birth date
(def ^:const max-required-age 100)

;; min age of adult
(def ^:const min-adult-age 18)

;; max range of suffix for generated birth code
(def ^:const birth-code-suffix-max-range 1000)
;; min year of birth date for which the birth code checksum wil be add to suffix
(def ^:const birth-code-suffix-crc-since-year 1954)
;; modulo used for birth code checksum calculation
(def ^:const birth-code-crc-modulo 11)

;; constant used for conditional enrichment of month part of birth code relevant for males
(def ^:const male-month-addition 0)
;; constant used for conditional enrichment of month part of birth code relevant for females
(def ^:const female-month-addition 50)
;; constant used for conditional enrichment of month part of birth code relevant for people born after 2004
(def ^:const after-2004-month-addition 20)

;; map used for random gender selection
(def ^:const number-gender-mapping {0 :male 1 :female})