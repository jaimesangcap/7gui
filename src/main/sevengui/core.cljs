(ns sevengui.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [sevengui.counter :refer [counter]]
    [sevengui.temperature-converter :refer [temperature-converter]]))

(defn root []
  [:div
   [counter]
   [:div [temperature-converter]]])

(rdom/render [root] (js/document.getElementById "root"))



