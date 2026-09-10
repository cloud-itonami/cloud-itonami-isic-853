(ns highereds.operation-test
  (:require [clojure.test :as t]
            [highereds.operation :as op]
            [highereds.store :as store]))

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

;; ----------------------------- execute-proposal! (real store wiring) -----------------------------

(t/deftest execute-proposal-commits-clean-proposal-at-phase-3
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-1" :schedule-enrollment-appointment :phase-3)]
    (t/is (= :committed (:status result)))
    (t/is (true? (:valid? result)))
    (t/is (= 1 (count (store/coordination-log s))))
    (t/is (= 1 (count (store/ledger s))))
    (t/is (= :committed (:t (first (store/ledger s)))))))

(t/deftest execute-proposal-always-escalates-safety-concern-even-at-phase-3
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-1" :flag-safety-concern :phase-3)]
    (t/is (= :escalated (:status result)))
    (t/is (= 0 (count (store/coordination-log s))) "never auto-commits")
    (t/is (= :approval-requested (:t (first (store/ledger s)))))
    (t/is (= "always-escalate-safety-concern" (:reason (first (store/ledger s)))))))

(t/deftest execute-proposal-requires-approval-below-phase-3
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-1" :schedule-enrollment-appointment :phase-1)]
    (t/is (= :escalated (:status result)))
    (t/is (= 0 (count (store/coordination-log s))))
    (t/is (= "phase-requires-approval" (:reason (first (store/ledger s)))))))

(t/deftest execute-proposal-holds-unverified-student
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-3" :coordinate-facility-booking :phase-2)]
    (t/is (= :held (:status result)))
    (t/is (false? (:valid? result)))
    (t/is (= 0 (count (store/coordination-log s))))
    (t/is (= :governor-hold (:t (first (store/ledger s)))))))

(t/deftest execute-proposal-rejects-when-phase-disallows-op
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-1" :schedule-enrollment-appointment :phase-0)]
    (t/is (= :rejected (:status result)))
    (t/is (= 0 (count (store/ledger s))) "no store write at all when the phase blocks the op")))

(t/deftest execute-proposal-safety-concern-allowed-from-phase-1
  ;; the bug this fix closes: :flag-safety-concern used to be phase-gated
  ;; OUT of phase-1/phase-2 entirely, so it could never even reach the
  ;; governor's always-escalate logic below phase 3.
  (let [s (store/seed-db)
        result (op/execute-proposal! s "student-1" :flag-safety-concern :phase-1)]
    (t/is (not= :rejected (:status result)))
    (t/is (= :escalated (:status result)))))

(t/deftest approve-and-commit-persists-an-escalated-proposal
  (let [s (store/seed-db)
        escalated (op/execute-proposal! s "student-1" :flag-safety-concern :phase-3)
        record (op/approve-and-commit! s (:proposal escalated) "advisor-1")]
    (t/is (= "student-1" (:student-id record)))
    (t/is (= 1 (count (store/coordination-log s))))
    (t/is (= 2 (count (store/ledger s))) "approval-requested + approval-granted")
    (t/is (= :approval-granted (:t (last (store/ledger s)))))
    (t/is (= "advisor-1" (:by (last (store/ledger s)))))))
