(ns sevengui.temperature-converter-test
  (:require
    [cljs.test :refer [deftest testing is]]
    ["@testing-library/react" :as rtl]
    [reagent.core :as r]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.temperature-converter :refer [temperature-converter]]))

(deftest temperature-converter-testing
  (doseq [[c f] [[-12 10.4]
                 [-5 23]
                 [10 50]
                 [15 59]
                 [35 95]
                 [41 105.8]]]
   (testing (str "should convert fahrenheit=" f " when updated celsius=" c)
     (with-component
       [temperature-converter]
       (fn [view]
         (let [fahrenheit (.getByRole view "textbox" #js {:name "fahrenheit"})
               celsius (.getByRole view "textbox" #js {:name "celsius"})]
           (.change rtl/fireEvent celsius #js {:target #js {:value c}})
           (is (= (.toString c) (.-value celsius)))
           (r/flush)
           (is (= (.toString f) (.-value fahrenheit))))))))

  (doseq [[c f] [[-17.78 0]
                 [-15 5]
                 [-13.33 8]
                 [7.22 45]
                 [15.56 60]
                 [29.44 85]
                 [38.89 102]]]
    (testing (str "should convert celsius=" c " when fahrenheit=" f " has been updated")
      (with-component
        [temperature-converter]
        (fn [view]
          (let [fahrenheit (.getByRole view "textbox" #js {:name "fahrenheit"})
                celsius (.getByRole view "textbox" #js {:name "celsius"})]
            (.change rtl/fireEvent fahrenheit #js {:target #js {:value f}})
            (is (= (.toString f) (.-value fahrenheit)))
            (r/flush)
            (is (= (.toString c) (.-value celsius))))))))

  (testing "should make fahrenheit empty when celsius is non-numeric"
    (with-component
      [temperature-converter]
      (fn [view]
        (let [fahrenheit (.getByRole view "textbox" #js {:name "fahrenheit"})
              celsius (.getByRole view "textbox" #js {:name "celsius"})]
          (.change rtl/fireEvent celsius #js {:target #js {:value "abc"}})
          (is (= "abc" (.-value celsius)))
          (r/flush)
          (is (= "" (.-value fahrenheit)))))))

  (testing "should make celsius empty when fahrenheit is non-numeric"
    (with-component
      [temperature-converter]
      (fn [view]
        (let [fahrenheit (.getByRole view "textbox" #js {:name "fahrenheit"})
              celsius (.getByRole view "textbox" #js {:name "celsius"})]
          (.change rtl/fireEvent fahrenheit #js {:target #js {:value "abc"}})
          (is (= "abc" (.-value fahrenheit)))
          (r/flush)
          (is (= "" (.-value celsius))))))))



(comment
  (for [[x y sum] [[1 2 3]
                   [1 1 2]
                   [1 5 6]]]
    (do (js/console.log "x " x)
        (js/console.log (str x " + " y " =  " sum)))))

