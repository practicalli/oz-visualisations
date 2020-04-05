(ns practicalli.mock-data)


(defn mock-data-set
  "Generates a set of mock data for each name

  Arguments: names as strings, names used in keys
  Returns: Sequence of maps, each representing confirmed cases"
  [& locations]
  (for [location locations
        day      (range 20)]
    {:day      day
     :location location
     :cases    (+ (Math/pow (* day (count location)) 0.8)
                  (rand-int (count location)))}))

#_
(mock-data-set "England" "Scotland" "Wales" "Northern Ireland")


;; UK Confirmed cases
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def uk-confirmed-cases
  [{:location "England" :cases 34707 }
   {:location "Scotland" :cases 3345 }
   {:location "Wales" :cases 2853 }
   {:location "Northern Ireland" :cases 988 }])
