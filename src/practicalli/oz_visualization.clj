(ns practicalli.oz-visualization
  (:require [oz.core :as oz]
            [practicalli.mock-data :as mock-data]))


(oz/start-server!)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Visualizing UK weather in April
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Data Generators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn random-temperature
  "Generated a random temperature given a miniumum and maximum"
  [minimum maximum]
  (+ (rand-int (- maximum minimum))
     minimum))

(defn random-precipitation
  "Generated a random percipitation level given a miniumum and maximum"
  [maximum]
  (/ (Math/round (* (rand 20) 10)) 10.0))

;; very basic data
(def weather-data
  [{:date "2020/04/01" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :rain}
   {:date "2020/04/02" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :drizzle}
   {:date "2020/04/03" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :rain}
   {:date "2020/04/04" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :snow}
   {:date "2020/04/05" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :fog}
   {:date "2020/04/06" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :drizzle}
   {:date "2020/04/07" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :rain}
   {:date "2020/04/08" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :sun}
   {:date "2020/04/09" :temperature (random-temperature 8 19) :precipitaion (random-precipitation 20) :weather :sun}])


;; Create a simple spread-plot to see the ranges of temperature

(def strip-plot
  {:data     {:values weather-data}
   :mark     "tick"
   :encoding {:x {:field "temperature"
                  :type  "quantitative"}}})

(oz/view! strip-plot)

;; Vega-lite provides defaults on how the data is visualized.
;; This keeps it simple to express visualizations language.
;; These defaults can be over-ridden by including them in the map.

;; Label axis
(def strip-plot-custom
  {:data     {:values weather-data}
   :mark     "tick"
   :encoding {:x {:field "temperature"
                  :type  "quantitative"
                  :scale {:type "linear" :domain [6 22]}
                  :axis  {:title "UK Temperature - April"}}}})

(oz/view! strip-plot-custom)

;; In the spread-plot we see the range of temperatures, but not the values for each day.
;; Create a histogram from the same data
;; Add a y axis showing how many days have the same temperature

(def weather-data-histogram
  {:data     {:values weather-data}
   :mark     "bar"
   :encoding {:x {:bin   true
                  :field "temperature"
                  :type  "quantitative"
                  :scale {:type "linear" :domain [6 22]}
                  :axis  {:title "UK Temperature - April"}}
              :y {:aggregate "count"
                  :type      "quantitative"}}})

(oz/view! weather-data-histogram)

;; A bigger data set would produce a more interesting bar chart

;; Adding Color
;; By adding color to the encoding, vega-lite compiler will stack the bars
;; giving a much more expressive visualization.

(def weather-data-histogram-color
  {:data     {:values weather-data}
   :mark     "bar"
   :encoding {:x     {:bin   true
                      :field "temperature"
                      :type  "quantitative"
                      :scale {:type "linear" :domain [6 22]}
                      :axis  {:title "UK Temperature - April"}}
              :y     {:aggregate "count"
                      :type      "quantitative"}
              :color {:field "weather"
                      :type  "nominal"}}})


(oz/view! weather-data-histogram-color)

;; The weather and temperature values are not related, so it makes for a confusing bar chart.
;; Its an indicator that the data we have needs improving


;; Generate Better Data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; How to generate better data?
;; (mapv #(hash-map :day %)
;;       (range 1 30))

;; (reduce #(merge weather-data-generator %)
;;         (mapv #(hash-map :day %) (range 1 30)))

;; (mapcat #(merge (hash-map :day %) weather-data-generator)
;;         (range 1 30))

;; (merge {:day 1} {:temp 1 :weather "snow"})


(defn weather-data-generator
  "Generate "
  [day]
  (let [temperature (rand-int 30)
        weather     (cond
                      (< temperature 2)                          "snow"
                      (and (> temperature 1) (< temperature 3))  "fog"
                      (and (>= temperature 3) (< temperature 7)) "rain"
                      (and (>= temperature 7) (< temperature 9)) "drizzle"
                      (>= temperature 9)                         "sun"
                      :otherwise                                 "changable")]
    {:day day :temperature temperature :weather weather}))

(mapv weather-data-generator (range 1 31))



(def weather-better-data-histogram-color
  {:data     {:values (mapv weather-data-generator (range 1 31))}
   :mark     "bar"
   :encoding {:x     {:bin   true
                      :field "temperature"
                      :type  "quantitative"
                      :scale {:type "linear" :domain [6 22]}
                      :axis  {:title "UK Temperature - April"}}
              :y     {:aggregate "count"
                      :type      "quantitative"}
              :color {:field "weather"
                      :type  "nominal"}}})

(oz/view! weather-better-data-histogram-color)

;; remove the scale to correct the visualization

(def weather-better-data-histogram-color
  {:data     {:values (mapv weather-data-generator (range 1 31))}
   :mark     "bar"
   :encoding {:x     {:bin   true
                      :field "temperature"
                      :type  "quantitative"
                      :axis  {:title "UK Temperature - April"}}
              :y     {:aggregate "count"
                      :type      "quantitative"}
              :color {:field "weather"
                      :type  "nominal"}}})

(oz/view! weather-better-data-histogram-color)


(def weather-better-data-histogram-color-custom
  {:data     {:values (mapv weather-data-generator (range 1 31))}
   :mark     "bar"
   :encoding {:x     {:bin   true
                      :field "temperature"
                      :type  "quantitative"
                      :axis  {:title "UK Temperature - April"}}
              :y     {:aggregate "count"
                      :type      "quantitative"}
              :color {:field "weather"
                      :type  "nominal"
                      :scale {:domain ["sun" "fog" "drizzle" "rain"  "snow"]
                              :range  ["#e7ba52" "#c7c7c7" "#aec7e8" "#1f77b4" "#9467bd"]}}}})


(oz/view! weather-better-data-histogram-color-custom)


;; Multi-view layouts
;; by type of weather

(def weather-better-data-histogram-color-custom-column
  {:data     {:values (mapv weather-data-generator (range 1 31))}
   :mark     "bar"
   :encoding {:x      {:bin   true
                       :field "temperature"
                       :type  "quantitative"
                       :axis  {:title "UK Temperature - April"}}
              :y      {:aggregate "count"
                       :type      "quantitative"}
              :column {:field "weather"
                       :type  "nominal"
                       :scale {:domain ["sun" "fog" "drizzle" "rain"  "snow"]
                               :range  ["#e7ba52" "#c7c7c7" "#aec7e8" "#1f77b4" "#9467bd"]}}}})


(oz/view! weather-better-data-histogram-color-custom-column)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Experimenting with Vega-lite visualizations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; https://vega.github.io/vega-lite/docs/

;; Line plots
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#_(def line-plot
    "Transform data for visualization"
    {:mark     "line"
     :data     {:values weather-data}
     :encoding {:x     {:field "day" :type "quantitative"}
                :y     {:field "cases" :type "quantitative"}
                :color {:field "location" :type "nominal"}}})


(def line-plot
  "Transform data for visualization"
  {:mark     "line"
   :data     {:values (mock-data/mock-data-set "England" "Scotland" "Wales" "Northern Ireland")}
   :encoding {:x     {:field "day" :type "quantitative"}
              :y     {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})

(oz/view! line-plot)

(oz/start-server!)

;; Vega-lite
;; {"$schema"   : "https://vega.github.io/schema/vega-lite/v4.json",
;;  "data"      : {"url" : "data/population.json"},
;;  "transform" : [{"filter" : "datum.year == 2000"}],
;;  "mark"      : "bar",
;;  "encoding"  : {"x" : {"aggregate" : "sum",
;;                        "field"     : "people",
;;                        "type"      : "quantitative",
;;                        "axis"      : {"title" : "population"}}}}

;; working
(def single-bar
  {:mark     "bar"
   :data     {:values [{:location "England" :cases 34707}]}
   :encoding {:x {:aggregate "sum"
                  :field     "cases"
                  :type      "quantitative"
                  :axis      {:title "location"}}}})

(oz/view! single-bar)

;; Fails - is this because of the layer ??
;; (def single-bar-labelled
;;   {:data     {:values [{:location "England" :cases 34707}]}
;;    :encoding {:x {:aggregate "sum"
;;                   :field     "cases"
;;                   :type      "quantitative"
;;                   :axis      {:title "Confirmed Cases - England"}}}
;;    :layer    [{:mark "bar"}
;;               {:mark {:type "text" :align "left" :baseline "middle" :dx 3}}
;;               {:encoding {:text {:field "location" :type "quantitative"}}}]})

;; (oz/view! single-bar-labelled)



;; Working
(def box-plot
  {:mark     {:type "boxplot" :extend 1.5}
   :data     {:values [{:location "England" :cases 10325}
                       {:location "England" :cases 21791}
                       {:location "England" :cases 34707}]}
   :encoding {:x {:field "cases"
                  :type  "quantitative"
                  :axis  {:title "Confirmed Cases - England"}}}})

(oz/view! box-plot)



;; Classic Gant chart
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vega-lite ranged bars
;; {"data"     : {"values" : [{"task" : "A", "start" : 1, "end" : 3},
;;                            {"task" : "B", "start" : 3, "end" : 8},
;;                            {"task" : "C", "start" : 8, "end" : 10}]},
;;  "mark"     : "bar",
;;  "encoding" : {"y"  : {"field" : "task", "type" : "ordinal"},
;;                "x"  : {"field" : "start", "type" : "quantitative"},
;;                "x2" : {"field" : "end"}}}


(def ranged-bars
  {:data     {:values [{"task" "Analysis", "start" 1, "end" 7},
                       {"task" "Design", "start" 7, "end" 13},
                       {"task" "Development", "start" 13, "end" 19}
                       {"task" "Fail", "start" 19, "end" 21}]},
   :mark     "bar",
   :encoding {:y  {:field "task", :type "ordinal"},
              :x  {:field "start", :type "quantitative"},
              :x2 {:field "end"}}} )

(oz/view! ranged-bars)

;; Pie Charts - fail to render
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; {"data"     : {"values" : [{"category" : 1, "value" : 4},
;;                            {"category" : 2, "value" : 6},
;;                            {"category" : 3, "value" : 10},
;;                            {"category" : 4, "value" : 3},
;;                            {"category" : 5, "value" : 7},
;;                            {"category" : 6, "value" : 8}]},
;;  "mark"     : "arc",
;;  "encoding" : {"theta" : {"field" : "value", "type" : "quantitative"},
;;                "color" : {"field" : "category", "type" : "nominal"}},
;;  "view"     : {"stroke" : null}}

;; fail
(def pie-chart
  {:data     {:values [{:category 1 :value 4}
                       {:category 2 :value 6}
                       {:category 3 :value 10}
                       {:category 4 :value 3}
                       {:category 5 :value 7}
                       {:category 6 :value 8}]}
   :mark     "arc"
   :encoding {:theta {:field "value" :type "quantitative"}
              :color {:field "category" :type "nominal"}}
   })

(oz/view! pie-chart)

;; fail
(def pie-chart-empty
  "Transform data for visualization"
  {:mark     "arc"
   :data     {:values
              []}
   :encoding {:theta {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})

(oz/view! pie-chart-empty)

;; fail
(def pie-chart-redux
  "Transform data for visualization"
  {:mark     "arc"
   :data     {:values
              [{:location "England" :cases 34707.0 }
               {:location "Scotland" :cases 3345.0 }
               {:location "Wales" :cases 2853.0 }
               {:location "NI" :cases 988.0 }]}
   :encoding {:theta {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})


(oz/view! pie-chart-redux)


;; Images
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; {"data"     : {"values" : [{"x" : 0.5, "y" : 0.5, "img" : "data/ffox.png"},
;;                            {"x" : 1.5, "y" : 1.5, "img" : "data/gimp.png"},
;;                            {"x" : 2.5, "y" : 2.5, "img" : "data/7zip.png"}]},
;;  "mark"     : {"type" : "image", "width" : 50, "height" : 50},
;;  "encoding" : {"x"   : {"field" : "x", "type" : "quantitative"},
;;                "y"   : {"field" : "y", "type" : "quantitative"},
;;                "url" : {"field" : "img", "type" : "nominal"}}}


(def image-plot
  {:data     {:values [{:x 0.5 :y 0.5 :img "data/ffox.png"}
                       {:x 1.5 :y 1.5 :img "data/gimp.png"}
                       {:x 2.5 :y 2.5 :img "data/obs.png"}]}
   :mark     {:type "image" :width 50 :height 50}
   :encoding {:x   {:field "x" :type "quantitative"}
              :y   {:field "y" :type "quantitative"}
              :url {:field "img" :type "nominal"}}
   })

(oz/view! image-plot)



;; Rule
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; https://vega.github.io/vega-lite/docs/rule.html

;; {"data"     : {"url" : "data/stocks.csv"},
;;  "mark"     : "rule",
;;  "encoding" : {"y"     : {"field"     : "price",
;;                           "type"      : "quantitative",
;;                           "aggregate" : "mean"},
;;                "size"  : {"value" : 2},
;;                "color" : {"field" : "symbol", "type" : "nominal"}}}

(def stocks-rule
  {:data     {:values [{:symbol "AAPL" :price 54}
                       {:symbol "AMZN" :price 87}
                       {:symbol "GOOG" :price 112}
                       {:symbol "IBM" :price 101}
                       {:symbol "MSFT" :price 66}]}
   :mark     "rule"
   :encoding {:y     {:field     "price"
                      :type      "quantitative"
                      :aggregate "mean"}
              :size  {:value 2}
              :color {:field "symbol" :type "nominal"}}
   })

(oz/view! stocks-rule)


;; Dashboard
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Putting all the examples together



(def dashboard
  [:div
   [:h1 "Oz Visualizations"]
   [:h2 "Exploring different styles (marks) of visualizations"]

   [:h3 "Visualising the weather"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:p "Simple Strip Plot"]
    [:vega-lite strip-plot]
    [:p "Simple Strip Plot with custom label"]
    [:vega-lite strip-plot-custom]]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:p "Weather histogram"]
    [:vega-lite weather-data-histogram]
    [:p "Weather histogram Color"]
    [:vega-lite weather-data-histogram-color]
    [:p "Weather histogram Color - better data"]
    [:vega-lite weather-better-data-histogram-color]]

   [:h3 "Layered and multi-views in vega-lite"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite weather-better-data-histogram-color-custom-column]]

   [:h3 "Experimenting with Vega-lite examples"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:h3 "Bar Charts"]
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:p "Simple Strip Plot"]
     [:vega-lite strip-plot]]
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:p "Strip Plot"]
     [:vega-lite strip-plot-custom]]
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:p "Single Bar"]
     [:vega-lite single-bar]]
    #_[:vega-lite single-bar-labelled] ; fails
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:p "Box plot"]
     [:vega-lite box-plot]]
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:p "Ranged bars - Waterfall Gant chart"]
     [:vega-lite ranged-bars]]]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:h3 "Line Plot"]
     [:vega-lite line-plot]]

    [:div {:style {:display "flex" :flex-direction "column"}}
     [:h3 "Image plots"]
     [:p "Open source software downloads"]
     [:vega-lite image-plot]
     ]
    [:div {:style {:display "flex" :flex-direction "column"}}
     [:h3 "Rules"]
     [:p "Width/height spanning rules"]
     [:vega-lite stocks-rule]]
    #_[:div {:style {:display "flex" :flex-direction "column"}}]

    ]])

(oz/view! dashboard)

;; Interesting debugging tip
;; including :vega-lite data without [] will show the actual data in the hiccup generated page


(oz/start-server!)








#_(def pie-chart-inner-radius
    "Transform data for visualization"
    {:mark     {:type "arc" :inner-radius 50}
     :data     {:values (mock-data-set "England" "Scotland" "Wales" "Northern Ireland")}
     :encoding {:x     {:field "day" :type "quantitative"}
                :y     {:field "cases" :type "quantitative"}
                :color {:field "location" :type "nominal"}}})

;; {
;;  "data"     : {
;;                "values" : [
;;                            {"category" : 1, "value" : 4},
;;                            {"category" : 2, "value" : 6},
;;                            {"category" : 3, "value" : 10},
;;                            {"category" : 4, "value" : 3},
;;                            {"category" : 5, "value" : 7},
;;                            {"category" : 6, "value" : 8}
;;                            ]
;;                },
;;  "mark"     : "arc",
;;  "encoding" : {
;;                "theta" : {"field" : "value", "type" : "quantitative"},
;;                "color" : {"field" : "category", "type" : "nominal"}
;;                },
;;  "view"     : {"stroke" : null}
;;  }
