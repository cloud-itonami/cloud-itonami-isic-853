(ns highereds.phase
  "Staged rollout phases for the higher-education actor.

  Phase 0: read-only
  Phase 1: enrollment-appointment scheduling only (approval-gated)
  Phase 2: + facility booking, supply, shift proposals (approval-gated)
  Phase 3: auto-commits clean proposals (safety concerns always escalate)")

(defn phase-description
  "Human-readable phase description."
  [phase]
  (case phase
    :phase-0 "Read-only (Phase 0)"
    :phase-1 "Enrollment-appointment scheduling (Phase 1, approval-gated)"
    :phase-2 "Facility booking, supply, shift proposals (Phase 2, approval-gated)"
    :phase-3 "Auto-commit clean proposals (Phase 3, safety concerns escalate)"
    "Unknown phase"))

(defn allowed-operations
  "Returns set of allowed operations in the current phase."
  [phase]
  (case phase
    :phase-0 #{}  ; read-only -- including safety flagging; phase 0 means no proposals at all
    :phase-1 #{:schedule-enrollment-appointment
               :flag-safety-concern}
    :phase-2 #{:schedule-enrollment-appointment
               :coordinate-facility-booking
               :coordinate-supply-request
               :schedule-staff-shift-proposal
               :flag-safety-concern}
    :phase-3 #{:schedule-enrollment-appointment
               :coordinate-facility-booking
               :coordinate-supply-request
               :schedule-staff-shift-proposal
               :flag-safety-concern}
    #{}))

(defn operation-allowed?
  "Returns true if the operation is allowed in the current phase."
  [phase op]
  (contains? (allowed-operations phase) op))

(defn requires-approval?
  "Returns true if the proposal requires human approval before commit
  (Phase 1–2). Phase 3+ auto-commits clean proposals."
  [phase _op]
  (case phase
    :phase-0 true
    :phase-1 true
    :phase-2 true
    :phase-3 false
    true))

(defn all-phases
  "Ordered list of all phases."
  []
  [:phase-0 :phase-1 :phase-2 :phase-3])
