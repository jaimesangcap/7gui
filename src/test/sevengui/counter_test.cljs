(ns sevengui.counter-test
  (:require
    [cljs.test :refer [deftest testing is]]
    ["@testing-library/react" :as rtl]
    [reagent.core :as r]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.counter :refer [counter]]))

(enable-console-print!)


(deftest counter-testing
  (testing "should increase by 1 on button click"
    (with-component [counter]
      (fn [view]
        (let [button (.getByRole view "button")
              input (.getByRole view "textbox")]
          (is (= "0" (.-value input)))
          (.click rtl/fireEvent button)
          (r/flush)
          (is (= "1" (.-value input))))))))




