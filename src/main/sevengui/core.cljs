(ns sevengui.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [sevengui.counter :refer [counter]]
    [sevengui.temperature-converter :refer [temperature-converter]]
    [sevengui.flight-booker :refer [flight-booker]]
    [sevengui.timer :refer [timer]]))

(defn root []
  [:div
   [:section
    [:h1 "Counter"]
    [counter]]
   [:section
    [:h1 "Temperature Converter"]
    [temperature-converter]]
   [:section
    [:h1 "Flight Booker"]
    [flight-booker]]
   [:section
    [:h1 "Timer"]
    [:div {:style {:width "200px"}}
     [timer]]]])

(rdom/render [root] (js/document.getElementById "root"))



