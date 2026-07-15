(ns highereds.operation-test
  (:require [clojure.test :as t]
            [highereds.operation :as op]))

(t/deftest valid-operations
  (t/is (op/valid-operation? :schedule-enrollment-appointment))
  (t/is (op/valid-operation? :coordinate-facility-booking))
  (t/is (op/valid-operation? :coordinate-supply-request))
  (t/is (op/valid-operation? :schedule-staff-shift-proposal))
  (t/is (op/valid-operation? :flag-safety-concern)))

(t/deftest invalid-operation
  (t/is (not (op/valid-operation? :unknown-op)))
  (t/is (not (op/valid-operation? :grading)))
  (t/is (not (op/valid-operation? :admissions-decision))))

(t/deftest all-operations
  (let [ops (op/all-operations)]
    (t/is (= 5 (count ops)))
    (t/is (every? op/valid-operation? ops))))

(t/deftest operation-descriptions
  (t/is (string? (op/operation-description :schedule-enrollment-appointment)))
  (t/is (string? (op/operation-description :coordinate-facility-booking)))
  (t/is (not (re-find #"Unknown" (op/operation-description :schedule-staff-shift-proposal)))))

(t/deftest unknown-operation-description
  (t/is (re-find #"Unknown" (op/operation-description :unknown))))
