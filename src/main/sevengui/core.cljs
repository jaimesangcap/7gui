(ns sevengui.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [sevengui.counter :refer [counter]]
    [sevengui.temperature-converter :refer [temperature-converter]]
    [sevengui.flight-booker :refer [flight-booker]]
    [sevengui.timer :refer [timer]]
    [sevengui.crud :refer [crud]]
    [sevengui.circle-drawer :refer [circle-drawer]]))

(defn root []
  [:div
   [:section
    [:h1 "1. Counter"]
    [counter]]
   [:section
    [:h1 "2. Temperature Converter"]
    [temperature-converter]]
   [:section
    [:h1 "3. Flight Booker"]
    [flight-booker]]
   [:section
    [:h1 "4. Timer"]
    [:div {:style {:width "200px"}}
     [timer]]]
   [:section {:style {:width "600px"}}
    [:h1 "5. CRUD"]
    [crud]]
   [:section
    [:h1 "6. Circle Drawer"]
    [circle-drawer]]])



(rdom/render [root] (js/document.getElementById "root"))



