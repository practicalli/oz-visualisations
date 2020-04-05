(ns practicalli.design-journal
  (:require [oz.core :as oz]))



;; Generating random data

;; Integers are easy with rand-int
;; To get a specific range just need a little tweaking

(defn random-temperature
  "Generated a random temperature given a miniumum and maximum"
  [minimum maximum]
  (+ (rand-int (- maximum minimum))
     minimum))


;; Decimal numbers are similarly easy to create
;; unless you wish to restrict the number of decimal places

(rand 20)

(float
  (rand 20))

(Math/round (rand 20))

(with-precision 10  (rand 20))


;; converting to a string is an easy way to get
;; a specific number of digits after the decimal place.

(format "%.2g" (rand 20))

;; or you can do the fancy way with Java
(import java.text.DecimalFormat)

(def decimal-format
  (DecimalFormat. "#.#"))

(.format decimal-format (rand 20))


;; thinking laterally
;; It we times the random number by 10,
;; then convert to an Integer we have the right characters we need
(float (/ (int (* (rand 20) 10)) 10))

;; Multiplying by a float value negates the need for an explicit cast.
(/ (int (* (rand 20) 10)) 10.0)


;; Rather than cast to an it, use Java Math round function for the same effect
;; This feels a little dfmore readable,
;; although its still a little bit of a hack.
(/ (Math/round (* (rand 20) 10)) 10.0)
