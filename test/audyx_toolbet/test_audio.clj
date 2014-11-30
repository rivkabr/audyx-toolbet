(ns audyx-toolbet.test-audio
  (:use [audyx-toolbet.audio]
        [midje.sweet]))

(defn abs[x]
  (max x (- x)))

(defn approx? [a b]
  (< (abs (max (- a b))) 0.00001))

(facts "Utils/Audio"
       (tabular 
         (fact "volume-splitted"
      (volume-splitted 10 ?num)  => (roughly ?res 0.1))
         ?num ?res
         1 10
         2 7
         4 4))
(fact
  "fft-frequencies"
      (count (fft-frequencies 1000 10)) => 5
      (apply max (fft-frequencies 1000 10)) => 400
      (fft-frequencies 1000 10) => (range 0 500 100)
 "pow and log"
      (pow 2 3) => 8.0
      (/ (log 8)(log 2)) => 3.0
      log10-const => (log 10)
      (log 1) => 0.0
 "db-to-gain and gain-to-db"
      (-> 0.2
        gain-to-db
        db-to-gain) => (partial approx? 0.2)
 "to-sec and to-msec"
      (-> 233
        to-sec
        to-msec) => 233
)
