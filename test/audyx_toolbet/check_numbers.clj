(ns audyx-toolbet.check-numbers
  (:use [audyx-toolbet.numbers])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))


(defn divides? [a b]
  (and (> a 0) 
       (= 0 (mod b a))))

(defspec check-factors-verifies 100
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)]
                (every? #(divides? % m) (factors m))))

(defspec check-factors-contains 100
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)
                 x gen/pos-int]
                (let [my-factors (apply hash-set (factors m))]
                  (if (and (> x 1) (divides? x m) (< x m))
                    (contains? my-factors x)
                    true))))

(defspec check-subsequent-factors-verifies-order 100 
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)]
                (apply < (subsequent-factors m))))

(defspec check-subsequent-factors-verifies-last 100 
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)]
                (= m (last (subsequent-factors m)))))

(defspec check-subsequent-factors-verifies-first 100 
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)]
                (= 1 (first (subsequent-factors m)))))

(defspec check-subsequent-factors-verifies 100 
  (prop/for-all [m (gen/such-that #(> % 1) gen/pos-int)]
                (let [my-factors (subsequent-factors m)]
                  (every?
                    identity 
                    (reductions #(if (divides? %1 %2) %2 false) my-factors)))))
