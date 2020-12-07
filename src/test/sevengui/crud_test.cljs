(ns sevengui.crud-test
  (:require
    [cljs.test :refer [deftest testing is]]
    ["@testing-library/react" :as rtl :refer [queries]]
    [reagent.core :as r]
    [sevengui.test-utils :refer [with-component]]
    [sevengui.crud :refer [crud]]))

(defn get-elements [view]
  {:name-input (.getByLabelText view #"Name" #js {:exact false})
   :surname-input (.getByLabelText view #"Surname" #js {:exact false})
   :filter-input (.getByLabelText view #"Filter" #js {:exact false})
   :create-button (.getByRole view "button" #js {:name "Create"})
   :update-button (.getByRole view "button" #js {:name "Update"})
   :delete-button (.getByRole view "button" #js {:name "Delete"})
   :listbox (.getByTestId view "listbox")})

(deftest crud-component-test
  (testing "should able to add a person"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input create-button  listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Jaime"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Sangcap"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.getByText queries listbox "Sangcap, Jaime")
          (is (= "" (.-value name-input)))
          (is (= "" (.-value surname-input)))))))

  (testing "should able to update a person"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input create-button update-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Mariely"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Palanas"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.click rtl/fireEvent (.getByText queries listbox "Palanas, Mariely"))
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Jaime"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Sangcap"}}))
          (is (= false (.-disabled update-button)))
          (.click rtl/fireEvent update-button)
          (r/flush)
          (.getByText queries listbox "Sangcap, Jaime")
          (is (= nil (.queryByText queries listbox "Palanas, Mariely")))))))

  (testing "should able to delete a person"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input create-button delete-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Nobody"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Im"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.click rtl/fireEvent (.getByText queries listbox "Im, Nobody"))
          (r/flush)
          (is (= false (.-disabled delete-button)))
          (.click rtl/fireEvent delete-button)
          (r/flush)
          (is (= nil (.queryByText queries listbox "Im, Nobody")))))))

  (testing "should able to filter a person by surname"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input filter-input create-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Jaime"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Sangcap"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Mariely"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Palanas"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Kleisli"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Sangcap"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.getByText queries listbox "Sangcap, Jaime")
          (.getByText queries listbox "Sangcap, Kleisli")
          (.getByText queries listbox "Palanas, Mariely")
          (.change rtl/fireEvent filter-input (clj->js {:target {:value "Sang"}}))
          (r/flush)
          (.getByText queries listbox "Sangcap, Jaime")
          (.getByText queries listbox "Sangcap, Kleisli")
          (is (= nil (.queryByText queries listbox "Palanas, Mariely")))
          (.change rtl/fireEvent filter-input (clj->js {:target {:value "Pala"}}))
          (r/flush)
          (.getByText queries listbox "Palanas, Mariely")
          (is (= nil (.queryByText queries listbox "Sangcap, Jaime")))
          (is (= nil (.queryByText queries listbox "Sangcap, Kleisli"))))))))


