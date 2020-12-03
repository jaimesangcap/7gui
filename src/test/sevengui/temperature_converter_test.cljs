(ns sevengui.temperature-converter-test
  (:require
    [cljs.test :refer [deftest testing is]]
    ["@testing-library/react" :as rtl]
    [reagent.core :as r]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.temperature-converter :refer [temperature-converter]]))

(deftest temperature-converter-testing
  (doseq [[c f] [[10 50]
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

  (doseq [[f c] [[91.4 33]
                 [98.6 37]
                 [73.4 23]
                 [25 -3.89]]]
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

  (testing "should not update fahrenheit when celsius is non-numeric"
    (with-component
      [temperature-converter]
      (fn [view]
        (let [fahrenheit (.getByRole view "textbox" #js {:name "fahrenheit"})
              celsius (.getByRole view "textbox" #js {:name "celsius"})]
          (.change rtl/fireEvent celsius #js {:target #js {:value "abc"}})
          (is (= "abc" (.-value celsius)))
          (r/flush)
          (is (= "" (.-value fahrenheit))))))))



(comment
  (for [[x y sum] [[1 2 3]
                   [1 1 2]
                   [1 5 6]]]
    (do (js/console.log "x " x)
        (js/console.log (str x " + " y " =  " sum)))))

