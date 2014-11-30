(ns audyx.utils.collections
  (:require  [clojure.string :as string]))


#+cljs 
(defn to-regular-array[arr]
  (IndexedSeq. arr 0))


(defn =without-keys? [obj-a obj-b keys-list]
  (apply = (map #(apply dissoc % keys-list) [obj-a obj-b])))

;(defn "convert a 2d vec to a hash-map.\n E.g. [[:a 1] [:b 2]] -> {:a 1 :b 2}" vec->map[vec]
(defn vec->map[vec]
  (into {} vec))

(defn map-2d-vec [f m]
  (map (fn[[k id]] [k (f id)]) m))

(defn map-object[f m]
  (vec->map (map-2d-vec f m)))


(defn map-object-with-key [f m]
  (into {} (map (fn [[a b]] [a (f a b)]) m)))

(defn map-reverse-hierarchy [m] ;http://stackoverflow.com/a/23653784/813665
  (or (apply merge-with conj
         (for [[k1 v1] m [k2 v2] v1] {k2 {k1 v2}}))
      {}))

(defn mean [x] 
  (if (empty? x) 0
    (/ (apply + x)
       (count x))))

(defn- range-with-end 
  ([end] [end (range end)])
  ([start end] [end (range start end)])
  ([start end steps] [end (range start end steps)]))

(defn range-till-end[& args]
  (let [[end lis] (apply range-with-end args)]
    (concat lis [end])))

(defn append-cyclic[lst a]
  (if (seq lst)
    (concat (rest lst) [a])
    lst))

(defn assoc-cyclic 
  ([coll k v]
   (if (contains? coll k)
     (assoc coll k v)
     (into {} (append-cyclic coll [k v]))))
  ([coll k v n]
   (if (< (count coll) n)
     (assoc coll k v)
     (assoc-cyclic coll k v))))

(defn max-and-min [x]
  (if (empty? x)
    [0 0]
    ((juxt #(apply max %) #(apply min %)) x)))

(defn compactize-map [m]
  (into {} (remove (comp nil? second) m)))

(defn abs[x]
  (max x (- x)))

(defn nearest-of-ss [ss x]
  (let [greater (first (subseq ss >= x))
        smaller (first (rsubseq ss <= x))]
    (apply min-key #(abs (- % x)) (remove nil? [greater smaller]))))

(defn nearest-of-seq[a b]
  (if (empty? a)
    b
    (map (partial nearest-of-ss (apply sorted-set a)) b)))

(defn map-to-object[f lst]
  (zipmap lst (map f lst)))

(defn map-to-object-with-index [f s]
    (into {} (map-indexed #(vector %1 (f %2)) s)))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn split-by-predicate [coll pred n] 
  "split a collection to items where the separator is a repetition of at least n elements that satisfy pred
  inspired by: http://stackoverflow.com/a/23555616/813665"
  (let [part  (partition-by  pred coll)
        ppart (partition-by (fn [x] (and
                                      (>= (count x) n) 
                                      (every? pred x))) part)]
        (map #(apply concat %) ppart)))

(defn positions [coll-of-lengths maximal-value]
  "receives a collection of lengths and returns a list of start and end positions"
  (let [end-pos (reductions + coll-of-lengths)
        start-pos (concat [0] end-pos)]
    (map #(list (min maximal-value %1) (min maximal-value %2)) start-pos end-pos)))

(defn subsequence [coll start end]
  (->> (drop start coll)
       (take (- end start))))

(defn split-by-predicate-opt [coll pred n d]
  (let [lengths (map #(* d %) (map count (split-by-predicate (take-nth d coll) pred (/ n d))))
        pos (positions lengths (count coll))]
    pos))

(defn index-of [s element]
  (or (ffirst (filter #(= (second %) element) (map-indexed #(vector %1 %2) s)))
      -1))

(defn display-sequence [long-seq short-seq value abs-step]
  (let [old-step (- (second short-seq) (first short-seq))
        step (* (- (second long-seq) (first long-seq)) abs-step)
        position-in-old-sequence (/ (- value (first short-seq)) old-step)]
    (cond
      (<= 0 position-in-old-sequence 4) (range (- value (* step position-in-old-sequence)) (+ value (* step (- 5 position-in-old-sequence))) step)
      (= position-in-old-sequence 5) (range (- value (* step (- position-in-old-sequence 1))) (+ value step) step)
      (empty? short-seq) (range (- value step) (+ value (* 4 step)) step)
      :else (range value (+ value (* 5 step)) step))))

(defn highest-below [m v]
  (second (last (sort-by first (group-by second (filter (fn [[x y]] (<= y v)) m))))))

(defn lowest-above [m v]
  (second (first (sort-by first (group-by second (filter (fn [[x y]] (>= y v)) m))))))

(defn find-keys-with-values-in [m s]
  (filter (comp s m) (keys m)))

(defn replace-keys [coll key-map]
  (zipmap (map #(get key-map % %) (keys coll)) (vals coll)))

(defn find-keys-with-value [m v]
  (find-keys-with-values-in m #{v}))

(defn linear [x a b aa bb]
  (+ aa (/ (* (- bb aa) (- x a)) (- b a))))
                        
(defn interpolate-linear [m v]
  (or (first (find-keys-with-value m v))
      (let [[below val-below] (last (sort (highest-below m v)))
            [above val-above] (first (sort (lowest-above m v)))]
      (when (and val-below val-above)
        (linear v val-below val-above below above)))))

(defn strings-to-keywords [strings]
  (map keyword (string/split strings #"\s+")))

(defn select-keys-in-order "http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html" [m keyseq]
  (map m keyseq))

(defn select-vals [map keyseq]
  (vals (select-keys map keyseq)))

(defmacro doseq-indexed "https://gist.github.com/halgari/4136116" [index-sym [item-sym coll] & body]                                                   
  `(doseq [[~item-sym ~index-sym]                                                                                                                             
           (map vector ~coll (range))]                                                                                                                        
       ~@body))  

(defn flatten-keys* [a ks m]
  (if (map? m)
    (if (seq m)
      (reduce into (map (fn [[k v]] (flatten-keys* a (conj ks k) v)) (seq m)))
      {})
    (assoc a ks m)))

(defn flatten-keys "http://blog.jayfields.com/2010/09/clojure-flatten-keys.html"
  [m] (flatten-keys* {} [] m))

(defn recursive-vals [m]
  (when m (vals (flatten-keys m))))

(defn sort-keys-by [a-func a-map]
  (map first (sort-by a-func a-map)))

(defn deep-merge
  [& xs]
  (if (every? map? xs)
    (apply merge-with deep-merge xs)
    (last xs)))
