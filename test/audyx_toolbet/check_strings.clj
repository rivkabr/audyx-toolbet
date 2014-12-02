(ns audyx-toolbet.check-strings
  (:use [audyx-toolbet.strings])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(defspec check-string-begins? 100
  (prop/for-all [s gen/string
  				 q gen/string]
                (let [res (string-begins? s q)]
                	(if (empty? q)
                    	(= res true)
                    	(if (= true res)
                    		(and
                    			(<= (count q) (count s))
                      			(= (first q) (first s)))
                    		true )))))
