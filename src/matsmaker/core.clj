(ns matsmaker.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;; Definitions:
;;; |Mats|, or materials, are the atoms of a crafting system. Mats have a price,
;;; denominated in copper coins.
;;; |Recipes| are comprised of mats and/or other recipes.
;;;
;;; An example:
;;; Let there be a mat :copper-ore.
;;; The recipe for :copper-ingot is:
;;;     {:copper-ore 2}.
;;; The recipe for a :copper-band is:
;;;     {:copper-ore 3}
;;; Therefore a :copper-band reduces to:
;;;     {:copper-ore 6}

;;; Mats is a top-level lookup of all mats.
(def +mats+
  {:linen-scrap             {:price 28        :type :cloth}
   :linen-spool             {:price (/ 320 10) :type :cloth}
   :copper-ore              {:price 17        :type :ore}
   :tin                     {:price (/ 80 10) :type :ore}
   :green-wood-log          {:price 11        :type :wood}
   :soft-wood-log           {:price 22        :type :wood}
   :seasoned-wood-log       {:price 0         :type :wood}
   :rawhide-leather-scrap {:price 7         :type :leather}
   :rugged-leather-scrap  {:price 24        :type :leather}
   })

(def +recipes+
  {;; Cloth
   :linen-bolt          {:linen-scrap 2}
   ;; Leather.
   :rugged-leather-cured {:rugged-leather-scrap 2}
   ;; Wood.
   :green-wood-plank    {:green-wood-log 2}
   :soft-wood-plank     {:soft-wood-log 4}
   :seasoned-wood-plank {:seasoned-wood-log 3}
   ;; Tailor.
   :linen-gloves        {:linen-bolt 2}
   :linen-coat-lining   {:linen-bolt 1}
   :linen-coat-panel    {:linen-bolt 4 :linen-spool 3 :rugged-leather-cured 1}
   ;; Artificer.
   :soft-focus-casing {:soft-wood-plank 3}
   :soft-focus-core   {:soft-wood-plank 2}
   ;; Jeweler.
   :copper-ingot     {:copper-ore 2}
   :copper-setting   {:copper-ore 2}
   :bronze-ingot     {:copper-ore 2 :tin 1}
   :copper-band      {:copper-ingot 3}
   :copper-hook      {:copper-ingot 2}})

(defn is-recipe? [x]
  (contains? +recipes+ x))

(defn is-mat? [x]
  (contains? +mats+ x))

(defn ingr-multiply [qty ingr]
  (let [ingrs (seq ingr)]
    (for [[k v] ingrs]
      [k (* qty v)])))

(defn eval-ingr [ingr1]
  ;; (println "eval-ing" ingr1)
  (let [[k v] ingr1
        ingr2 (+recipes+ k)]
    (ingr-multiply v ingr2)))

(defn overflow? [acc rs]
  (let [acnt (count acc)
        rcnt (count rs)]
    (or (> acnt 5) (> rcnt 5))))

(defn eval-recipe [recip]
  (loop [acc [] rs (vec recip)]
    ;; (println "acc is" acc "and recipe is" rs)
    (let [r (first rs)
          [ingr qty] r]
      ;; (println "processing" r)
      (cond
       ;; (overflow? acc rs) (do (println "overflow :(") (println "acc" acc "rs" rs))
       (empty? rs) acc
       (is-mat? ingr)
         (recur (conj acc r) (rest rs))
       (is-recipe? ingr)
         (recur acc (concat (eval-ingr r) (rest rs)))
       :else (println "Invalid mat or recipe:" r)))))

(defn reduce-recipe [recip]
  (->> recip
       (map #(apply hash-map %))
       (reduce #(merge-with + %1 %2))))

(defn mat-price-for
  ([mat]
     (price-of mat 1))
  ([mat qty]
     (* qty (get-in +mats+ [mat :price]))))

(defn calc-cost [ms]
  (->> ms
       (map (fn [[k v]] (mat-price-for k v)))
       (reduce +)))

(defn price-for [recip]
  (-> recip
      (eval-recipe)
      (reduce-recipe)
      (calc-cost)))
