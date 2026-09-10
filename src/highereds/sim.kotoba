(ns highereds.sim
  "Demo/test harness for the higher-education actor."
  (:require [kotoba.lang.text :as str]
            [highereds.store :as store]
            [highereds.advisor :as advisor]
            [highereds.governor :as governor]
            [highereds.phase :as phase]
            [highereds.operation :as op]))

(defn run-demo
  "Run a demonstration of the actor with sample proposals.
  Prints decision trace to stdout."
  []
  (println "\n=== cloud-itonami-isic-853 Higher Education Actor Demo ===\n")

  (let [s (store/seed-db)]
    (println "Initial store:")
    (println (str "  " (count (store/all-students s)) " registered students"))
    (println (str "  Ledger entries: " (count (store/ledger s))))
    (newline)

    ;; Scenario 1: Valid enrollment appointment proposal
    (println "Scenario 1: Valid enrollment appointment proposal")
    (let [proposal (advisor/advisor s "student-1" :schedule-enrollment-appointment :phase-1)
          decision (governor/govern s proposal)]
      (println (str "  Student: " (:student-id proposal)))
      (println (str "  Operation: " (:op proposal)))
      (println (str "  Governor decision: " (:action decision)))
      (println (str "  Valid: " (:valid? decision)))
      (if (seq (:violations decision))
        (println (str "  Violations: " (str/join "; " (:violations decision)))))
      (newline))

    ;; Scenario 2: Safety concern (always escalates)
    (println "Scenario 2: Safety concern (always escalates)")
    (let [proposal (advisor/advisor s "student-2" :flag-safety-concern :phase-3)
          decision (governor/govern s proposal)]
      (println (str "  Student: " (:student-id proposal)))
      (println (str "  Operation: " (:op proposal)))
      (println (str "  Governor decision: " (:action decision)))
      (println (str "  Valid: " (:valid? decision)))
      (newline))

    ;; Scenario 3: Unverified student (blocked)
    (println "Scenario 3: Unverified student (blocked)")
    (let [proposal (advisor/advisor s "student-3" :coordinate-facility-booking :phase-2)
          decision (governor/govern s proposal)]
      (println (str "  Student: " (:student-id proposal)))
      (println (str "  Operation: " (:op proposal)))
      (println (str "  Governor decision: " (:action decision)))
      (println (str "  Valid: " (:valid? decision)))
      (if (seq (:violations decision))
        (doseq [v (:violations decision)]
          (println (str "    - " v))))
      (newline))

    ;; Scenario 4: Non-existent student (blocked)
    (println "Scenario 4: Non-existent student (blocked)")
    (let [proposal (advisor/advisor s "student-999" :schedule-staff-shift-proposal :phase-2)
          decision (governor/govern s proposal)]
      (println (str "  Student: " (:student-id proposal)))
      (println (str "  Operation: " (:op proposal)))
      (println (str "  Governor decision: " (:action decision)))
      (println (str "  Valid: " (:valid? decision)))
      (if (seq (:violations decision))
        (doseq [v (:violations decision)]
          (println (str "    - " v))))
      (newline))

    ;; Phase information
    (println "Rollout phases:")
    (doseq [p (phase/all-phases)]
      (let [ops (phase/allowed-operations p)]
        (println (str "  " (phase/phase-description p)))
        (if (seq ops)
          (println (str "    Allowed ops: " (str/join ", " (map name ops))))
          (println "    No operations allowed (read-only)"))))
    (newline)

    ;; Operations
    (println "Closed allowlist of operations:")
    (doseq [operation (op/all-operations)]
      (println (str "  - " (op/operation-description operation))))
    (newline))

  (println "Demo completed successfully.")
  nil)

;; Entry point
(defn -main
  [& _args]
  (run-demo))
