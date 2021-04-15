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
          (.change rtl/fireEvent name-input (clj->js {:target {:value "John"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Doe"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.getByText queries listbox "Doe, John")
          (is (= "" (.-value name-input)))
          (is (= "" (.-value surname-input)))))))

  (testing "should able to update a person"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input create-button update-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "John"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Doe"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.click rtl/fireEvent (.getByText queries listbox "Doe, John"))
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Red"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Salmon"}}))
          (is (= false (.-disabled update-button)))
          (.click rtl/fireEvent update-button)
          (r/flush)
          (.getByText queries listbox "Salmon, Red")
          (is (= nil (.queryByText queries listbox "Doe, John")))))))

  (testing "should able to delete a person"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input create-button delete-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "John"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Doe"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.click rtl/fireEvent (.getByText queries listbox "Doe, John"))
          (r/flush)
          (is (= false (.-disabled delete-button)))
          (.click rtl/fireEvent delete-button)
          (r/flush)
          (is (= nil (.queryByText queries listbox "Doe, John")))))))

  (testing "should able to filter a person by surname"
    (with-component
      [crud]
      (fn [view]
        (let [{:keys [name-input surname-input filter-input create-button listbox]} (get-elements view)]
          (.change rtl/fireEvent name-input (clj->js {:target {:value "John"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Doe"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Jane"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Doe"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.change rtl/fireEvent name-input (clj->js {:target {:value "Ann"}}))
          (.change rtl/fireEvent surname-input (clj->js {:target {:value "Chovey"}}))
          (r/flush)
          (.click rtl/fireEvent create-button)
          (r/flush)
          (.getByText queries listbox "Doe, John")
          (.getByText queries listbox "Doe, Jane")
          (.getByText queries listbox "Chovey, Ann")
          (.change rtl/fireEvent filter-input (clj->js {:target {:value "Do"}}))
          (r/flush)
          (.getByText queries listbox "Doe, John")
          (.getByText queries listbox "Doe, Jane")
          (is (= nil (.queryByText queries listbox "Chovey, Ann")))
          (.change rtl/fireEvent filter-input (clj->js {:target {:value "Cho"}}))
          (r/flush)
          (.getByText queries listbox "Chovey, Ann")
          (is (= nil (.queryByText queries listbox "Doe, John")))
          (is (= nil (.queryByText queries listbox "Doe, Jane"))))))))


