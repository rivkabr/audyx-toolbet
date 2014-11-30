(ns  audyx.utils.google-charts
   #+cljs (:use-macros [audyx.macros.debugging :only [dbg dbgfn]]
               [purnam.core :only [? !>]])
  (:require [audyx.utils.collections :as collections]))


(defn value[x]
  {:v x})

(defn several-values [vv]
  (map value vv))

(defn map-rows[m keys-in-order]
  (let [mm (sort m)]
    (map (fn [[k v]] {:c (cons (value k) (several-values (collections/select-keys-in-order v keys-in-order)))}) mm)))

(defn prepare-rows[m keys-in-order]
  (map-rows (collections/map-reverse-hierarchy m) keys-in-order))

(defn merge-series [a b]
  (let [series-concat (concat a b)
        series-data (map #(nth % 1) series-concat)
        series-keys (range (count series-data))]
    (zipmap series-keys series-data)))

#+cljs (defn add-column-for-hide-columns [data]
  (let [klass (? js/google.visualization.DataTable) 
        data-tbl (new klass (clj->js data))]
    (!> data-tbl.addColumn "number" nil nil)
    (js->clj data-tbl)))

(defn add-serie-for-hide-columns [series]
  (merge-series series {0 {:color "transparent"}}))

#+cljs (defn merge-data "http://jsfiddle.net/asgallant/XF7JE/ and https://developers.google.com/chart/interactive/docs/reference#google_visualization_data_join"
  [a b number-of-series-a number-of-series-b]
  (if (seq a)
    (let [klass (? js/google.visualization.DataTable) 
          a (new klass (clj->js a))
          b (new klass (clj->js b))
          [ca cb] (map #(clj->js (range 1 (inc %))) [number-of-series-a number-of-series-b])
          data-temp (!> js/google.visualization.data.join a b "full" (clj->js [[0, 0]]) ca cb)]
      (js->clj (!> data-temp.toJSON)))
    b))

#+cljs (defn merge-data-and-series [graphs]
  (loop [remaining-graphs graphs
         res-data {}
         res-serie {}]
    (if (seq remaining-graphs)
      (let [{:keys [data options]} (first remaining-graphs)
            serie (:series options)]
      	(recur (rest remaining-graphs)
               (merge-data res-data data (count res-serie) (count serie))
               (merge-series res-serie serie)))
      {:data res-data :series res-serie})))

(defn set-columns [nbr-columns hide-columns]
  (let [columns (range 0 nbr-columns)
        temp (repeat (count hide-columns) nbr-columns)
        predicat (zipmap hide-columns temp)]
    (replace predicat columns)))


#+cljs (defn add-annotation-to-columns [columns scalars hearings dates calc-tooltip]
  (let [columns-index (range 1 (count columns))
        columns-annotations (map (fn [col scalar hearing date] 
                                    {
                                     :type "string" 
                                     :properties {:role "tooltip" :html true} 
                                     :calc (partial calc-tooltip col scalar hearing date)
                                     }) columns-index scalars hearings dates)
        columns-to-annotate (rest columns)
        columns-without-x (interleave columns-to-annotate columns-annotations)
        columns-res (concat (list (first columns)) columns-without-x)]
    columns-res))

#+cljs (defn join-series [graphs]
  (let [series (:series (merge-data-and-series graphs))]
    (add-serie-for-hide-columns series)))

#+cljs (defn join-data [graphs]
  (let [data (:data (merge-data-and-series graphs))]
    (add-column-for-hide-columns data)))

#+cljs (defn join-graphs [graph-pristine {:keys [graphs scalars hearings dates]} hide-columns calc-tooltip]
  (let [final-data (join-data graphs)
        final-series (join-series graphs)
        res-with-series (assoc-in graph-pristine [:options :series] final-series)]
    (merge res-with-series {:data final-data :view {:columns (clj->js (add-annotation-to-columns (set-columns (count final-series) hide-columns) scalars hearings dates calc-tooltip))}})))



#+cljs (defn join-chart[graph a b number-of-series-a number-of-series-b]
  (assoc graph :data (merge-data a b number-of-series-a number-of-series-b) ))



