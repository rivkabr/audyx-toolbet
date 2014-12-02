(ns audyx-toolbet.check-collections
  (:use [audyx-toolbet.collections])
  (:require  [miner.herbert :as h]
            [miner.herbert.generators :as hg]
            [clojure.test.check :as tc]
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

(defspec check-max-and-min-h 100
  (hg/property (fn [v] 
                 (let [[the-max the-min] (max-and-min v)]
                   (and
                     (= the-min (apply min v))
                     (= the-max (apply max v)))))
               '[int+]))




(defspec check-flatten-keys 100
  (prop/for-all [mmm (gen/such-that not-empty (gen/map gen/keyword gen/int))]
                (let [m {:a mmm}
                      res (flatten-keys m)]
                    (every? true? (map #(= (get res %) (get-in m %)) (keys res))))))

(defspec check-append-cyclic 100
  (hg/property (fn [v x]
                 (let [res (append-cyclic v x)]
                   (and (= (count res) (count v))
                        (= x (last res))
                        (= (first res) (second v))
                        (= (drop-last 1 res) (rest v)))))
               '[int+ 2] 'int))


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
;;;

#(defspec check-unflatten-keys 100
  (prop/for-all [m (gen/map (gen/such-that not-empty (gen/vector gen/int)) gen/int)]
    (= m (flatten-keys (unflatten-keys m)))))

(defspec check-flatten-keys 100
  (prop/for-all [m (gen/recursive-gen 
                      (fn [inner] (gen/such-that not-empty (gen/map gen/int inner)))
                      gen/int)]
    (= m (unflatten-keys (flatten-keys m)))))


