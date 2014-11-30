(ns audyx-toolbet.numbers)

(defn factors [x]
  "integer -> vector[integers]"
  (loop [xf [] i 2]
    (if (> (* i i) x)
      (vec (sort (distinct xf)))
      (if (zero? (rem x i))
        (recur (conj xf i (/ x i)) (inc i))
        (recur xf (inc i))))))


(defn greatest-factor [x]
  (or (last (factors x)) 1))

(defn subsequent-factors [x]
  (loop [n x
         res []]
    (let [next-res (cons n res)]
      (if (<= n 1)
        next-res
        (recur (greatest-factor n) next-res)))))
