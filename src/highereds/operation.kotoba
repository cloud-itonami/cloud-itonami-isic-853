(ns highereds.operation
  "Closed proposal-operation allowlist for the higher-education actor,
  plus `execute-proposal!` -- the dispatcher that ties the advisor's
  proposal to the governor's decision and the phase gate, and actually
  writes the outcome to the store. `highereds.governor/govern` only
  DECIDES (:hold | :commit | :escalate); nothing previously wired that
  decision back into a store mutation or an audit-ledger fact, unlike
  every sibling actor's `operation.cljc`/StateGraph `:commit` node.

  Higher-education students are typically legal adults, but the actor
  remains coordination-only: enrollment/advising appointments, facility
  booking, administrative supply coordination, staff shift proposals,
  and safety-concern escalation. Never academic judgment, admissions
  decisions, academic standing determinations, degree conferral, or
  disciplinary action."
  (:require [highereds.advisor :as advisor]
            [highereds.governor :as governor]
            [highereds.phase :as phase]
            [highereds.store :as store]))

(defn valid-operation?
  "Returns true if op is in the closed allowlist."
  [op]
  (#{:schedule-enrollment-appointment
     :coordinate-facility-booking
     :coordinate-supply-request
     :schedule-staff-shift-proposal
     :flag-safety-concern}
   op))

(defn all-operations
  "Closed allowlist of operations."
  []
  [:schedule-enrollment-appointment
   :coordinate-facility-booking
   :coordinate-supply-request
   :schedule-staff-shift-proposal
   :flag-safety-concern])

(defn operation-description
  "Human-readable description of each operation."
  [op]
  (case op
    :schedule-enrollment-appointment "Schedule enrollment/advising appointment"
    :coordinate-facility-booking "Coordinate facility/classroom booking"
    :coordinate-supply-request "Coordinate administrative supply request"
    :schedule-staff-shift-proposal "Propose staff shift/coverage arrangement"
    :flag-safety-concern "Escalate safety/wellbeing concern"
    "Unknown operation"))

;; ----------------------------- dispatch (advisor -> governor -> store) -----------------------------

(defn execute-proposal!
  "Runs the advisor, the governor, and the phase gate for `op` against
  `student-id`, then writes the REAL outcome to `store`:
    - phase disallows the op entirely -> {:status :rejected}, no store write
    - governor HARD-holds (unverified student / non-:propose effect /
      scope-excluded) -> {:status :held ...}, an audit-ledger hold fact,
      never committed
    - governor says :escalate (always true for :flag-safety-concern), OR
      the phase still requires human approval -> {:status :escalated
      :proposal ...}, an audit-ledger approval-requested fact, never
      auto-committed -- see `approve-and-commit!` for the human step
    - otherwise -> {:status :committed :record ...}, the record is
      persisted via `store/commit-record!` and a committed fact is
      logged
  Returns the governor's decision merged with :status (and :proposal
  when escalated, so a caller can pass it to `approve-and-commit!`)."
  [store student-id op phase-kw]
  (if-not (phase/operation-allowed? phase-kw op)
    {:status :rejected :reason "operation-not-allowed-in-phase" :phase phase-kw :op op}
    (let [proposal (advisor/advisor store student-id op phase-kw)
          decision (governor/govern store proposal)]
      (cond
        (not (:valid? decision))
        (do (store/append-ledger! store {:t :governor-hold :op op :student-id student-id
                                         :disposition :hold :violations (:violations decision)})
            (assoc decision :status :held))

        (or (= :escalate (:action decision)) (phase/requires-approval? phase-kw op))
        (do (store/append-ledger! store
              {:t :approval-requested :op op :student-id student-id :disposition :escalate
               :reason (if (= :escalate (:action decision))
                         "always-escalate-safety-concern" "phase-requires-approval")})
            (assoc decision :status :escalated :proposal proposal))

        :else
        (let [record {:op op :student-id student-id :value (dissoc proposal :created-at)}]
          (store/commit-record! store record)
          (store/append-ledger! store {:t :committed :op op :student-id student-id :disposition :commit})
          (assoc decision :status :committed :record record))))))

(defn approve-and-commit!
  "A human operator approves a proposal previously escalated by
  `execute-proposal!` (its :proposal value); commits it now."
  [store proposal approved-by]
  (let [{:keys [op student-id]} proposal
        record {:op op :student-id student-id :value (dissoc proposal :created-at)}]
    (store/commit-record! store record)
    (store/append-ledger! store {:t :approval-granted :op op :student-id student-id
                                 :by approved-by :disposition :commit})
    record))
