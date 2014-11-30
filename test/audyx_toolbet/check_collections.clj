(ns audyx-toolbet.check-collections
  (:use [audyx-toolbet.collections])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))


(defspec check-select-keys-in-order-count 10
  (prop/for-all [m (gen/map gen/keyword gen/int)
                 keyseq (gen/vector gen/keyword)]
                (let [res (select-keys-in-order m keyseq)]
                  (= (count res) (count keyseq)))))

(defspec check-select-keys-in-order 10
  (prop/for-all [m (gen/map gen/keyword gen/int)
                 keyseq (gen/vector gen/keyword)]
                (let [res (select-keys-in-order m keyseq)]
                  (every? identity (map #(= (get m %1) %2) keyseq res)))))



(defspec check-max-and-min 11
  (prop/for-all [v (gen/vector gen/int)]
                (let [[the-max the-min] (max-and-min v)]
                  (if (empty? v)
                    (= the-max the-min 0)
                    (and
                      (= the-min (apply min v))
                      (= the-max (apply max v)))))))

(defspec check-flatten-keys 10
  (prop/for-all [mmm (gen/such-that not-empty (gen/map gen/keyword gen/int))]
                (let [m {:a mmm}
                      res (flatten-keys m)]
                    (every? true? (map #(= (get res %) (get-in m %)) (keys res))))))

(defspec check-append-cyclic 12
  (prop/for-all [v (gen/such-that #(> (count %) 1) (gen/vector gen/int))
                 x gen/int]
                (let [res (append-cyclic v x)]
                  (and (= (count res) (count v))
                       (= x (last res))
                       (= (first res) (second v))
                       (= (drop-last 1 res) (rest v))))))


(defn dist [a b]
  (abs (- a b)))

(defn distances [coll x]
  (map #(dist x %) coll))

(defn min-dist [coll x]
  (if (empty? coll) 0
    (apply min (distances coll x))))

(defspec check-nearest-of-seq 30
  (prop/for-all [a (gen/vector gen/int)
                 b (gen/vector gen/int)]
                (let [res (nearest-of-seq a b)]
                  (= (map #(min-dist a %) b)
                     (map dist res b)))))
