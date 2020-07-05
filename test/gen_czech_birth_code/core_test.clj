(ns gen-czech-birth-code.core-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]

            [gen-czech-birth-code.core :refer :all]
            [gen-czech-birth-code.config :as cfg-ut]
            [gen-czech-birth-code.test-support :refer :all :as tst-sup]))

(deftest test-min-rand-offset-days
  (testing "Test if computation of min offset range is equal to one year."
    (is (= 365 (tst-sup/min-rand-offset-days)))))

(deftest test-max-rand-offset-days
  (testing "Test if computation of max offset range is equal to 99 years."
    (is (= 36135 (tst-sup/max-rand-offset-days)))))

(deftest test-generated-offset-ranges
  (testing "Check if all randomly generated numbers are inside the 'offset days' interval."
    (let [rand-offset-days (take 1000000 (repeatedly #(get-random-offset-days)))]
      (is (>= (apply min rand-offset-days) (tst-sup/min-rand-offset-days)))
      (is (<= (apply max rand-offset-days) (+ (tst-sup/min-rand-offset-days) (tst-sup/max-rand-offset-days)))))))

(deftest test-sysdate-minus-offset-days
  (testing "Test substraction of days from date."
    (are [expected actual] (= (date-only expected) (date-only actual))
      (time/now) (sysdate-minus-offset-days 0)
      (time/minus (time/now) (time/days 1)) (sysdate-minus-offset-days 1))))

(deftest test-gender-month-addition
  (testing "Test the gender addition value for month part of birth code when input gender is known."
    (are [expected actual] (= expected actual)
      0 (gender-month-addition :male)
      50 (gender-month-addition :female))))

(deftest test-gender-month-addition-exception
  (testing "Test exception when passed input gender is unknown."
    (is (thrown? IllegalArgumentException (gender-month-addition :unknown-gender)))))

(deftest test-month-birth-code-addition
  (testing "Test the addition value (according to year as well as gender) value for month part of birth code."
    (are [expected actual] (= expected actual)
      0 (month-birth-code-addition :male 2003)
      20 (month-birth-code-addition :male 2004)
      50 (month-birth-code-addition :female 2003)
      70 (month-birth-code-addition :female 2004))))

(deftest test-calculate-birt-code-crc
  (testing "Test birth code checksum calculation."
    (are [expected actual] (= expected actual)
      0 (calculate-birt-code-crc "2002" "03" "13" "436") ; 0
      1 (calculate-birt-code-crc "2020" "59" "10" "871") ; 1
      2 (calculate-birt-code-crc "2015" "04" "25" "409") ; 2
      3 (calculate-birt-code-crc "2016" "07" "13" "226") ; 3
      4 (calculate-birt-code-crc "2007" "09" "27" "003") ; 4
      5 (calculate-birt-code-crc "1984" "52" "09" "029") ; 5
      6 (calculate-birt-code-crc "2015" "06" "08" "717") ; 6
      7 (calculate-birt-code-crc "1974" "09" "20" "704") ; 7
      8 (calculate-birt-code-crc "2008" "05" "23" "220") ; 8
      9 (calculate-birt-code-crc "1976" "05" "02" "477") ; 9
      0 (calculate-birt-code-crc "1949" "55" "22" "059")))) ; 10

(deftest test-calculate-suffix
  (testing "Test birth code suffix part calculation."
    (are [expected actual] (= expected actual)
      "333" (calculate-suffix "1953" "12" "12" "333") ; without crc
      "4360" (calculate-suffix "2002" "03" "13" "436") ; crc=0
      "8711" (calculate-suffix "2020" "59" "10" "871") ; crc=1
      "4092" (calculate-suffix "2015" "04" "25" "409") ; crc=2
      "2263" (calculate-suffix "2016" "07" "13" "226") ; crc=3
      "0034" (calculate-suffix "2007" "09" "27" "003") ; crc=4
      "0295" (calculate-suffix "1984" "52" "09" "029") ; crc=5
      "7176" (calculate-suffix "2015" "06" "08" "717") ; crc=6
      "7047" (calculate-suffix "1974" "09" "20" "704") ; crc=7
      "2208" (calculate-suffix "2008" "05" "23" "220") ; crc=8
      "4779" (calculate-suffix "1976" "05" "02" "477") ; crc=9
      "059" (calculate-suffix "1949" "55" "22" "059")))) ; crc=10

(deftest test-to-birth-code-data-bcode-part
  (testing "Test generated birth code value (without suffix part)."
    (are [expected actual] (= expected (first actual))
      "916123" (to-birth-code-data :female (time/date-time 1991 11 23))
      "911123" (to-birth-code-data :male (time/date-time 1991 11 23))
      "117309" (to-birth-code-data :female (time/date-time 2011 3 9))
      "112309" (to-birth-code-data :male (time/date-time 2011 3 9)))))

(deftest test-to-birth-code-data-suffix-length
  (testing "Test correct digits count of birth code suffix generated according to given gender and birth date."
    (are [expected actual] (= expected (count (last actual)))
      3 (to-birth-code-data :female (time/date-time 1953 11 23))
      3 (to-birth-code-data :male (time/date-time 1953 11 23))
      4 (to-birth-code-data :female (time/date-time 1954 3 9))
      4 (to-birth-code-data :male (time/date-time 1954 3 9)))))
