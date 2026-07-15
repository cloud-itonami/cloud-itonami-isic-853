(ns highereds.store
  "SSoT for the ISIC-853 higher-education COORDINATION actor,
  behind a `Store` protocol so the backend is a swap, not a rewrite -- the
  same seam every `cloud-itonami-isic-*` actor in this fleet uses.

  This actor coordinates the back-office operations of a higher-education
  institution (university, college): enrollment/advising appointment scheduling,
  facility/classroom booking coordination, consumable supply coordination
  (office/admin supplies), staff shift proposals, and safety-concern flagging
  (wellbeing incidents, mental-health risk signals). It never touches grading,
  academic assessment, curriculum selection, pedagogical decisions, admissions
  decisions, academic standing/probation/expulsion determinations, degree
  conferral, disciplinary action, or any safety-authority override -- see
  `highereds.governor`'s `scope-exclusion-violations`, a HARD, permanent,
  un-overridable block.

  `MemStore` -- atom of EDN. The deterministic default for dev/tests/demo
  (no deps). A `students` directory keyed by `:student-id` STRING (never a
  keyword -- consistent keying from the start, avoiding the silent-miss
  bug that plagued an earlier shepherd attempt).

  A registered/verified student record must exist before ANY proposal
  for that student may ever commit or escalate -- `highereds.governor`'s
  `student-unverified-violations` re-derives this from the student's own
  `:registered?`/`:verified?` fields, never from proposal self-report,
  the SAME 'ground truth, not self-report' discipline every sibling
  actor's own governor uses.

  The ledger stays append-only: which student a proposal targeted, which
  operation, on what basis, committed/held/escalated and approved by
  whom is always a query over an immutable log.")

(defprotocol Store
  (student [s student-id] "Registered student record, or nil.
    Student map: {:student-id .. :name .. :enrollment-status .. :registered? bool :verified? bool}.")
  (all-students [s])
  (ledger [s] "the append-only immutable decision-fact log")
  (coordination-log [s] "the append-only committed coordination-proposal history")
  (commit-record! [s record] "apply a committed proposal's record to the SSoT")
  (append-ledger! [s fact] "append one immutable decision fact")
  (with-students [s students] "replace/seed the student directory (map student-id->student)"))

;; ----------------------------- demo data -----------------------------

(defn demo-data
  "A small, self-contained student directory covering both the happy path
  and the governor's own hard checks, so the actor + tests run offline."
  []
  {:students
   {"student-1" {:student-id "student-1" :name "Aiko Yamada" :enrollment-status "active"
                  :registered? true :verified? true}
    "student-2" {:student-id "student-2" :name "Kenji Sato" :enrollment-status "active"
                  :registered? true :verified? true}
    "student-3" {:student-id "student-3" :name "Yuki Nakamura" :enrollment-status "active"
                  :registered? true :verified? false}}})

;; ----------------------------- MemStore (default) -----------------------------

(defrecord MemStore [a]
  Store
  (student [_ student-id] (get-in @a [:students student-id]))
  (all-students [_] (sort-by :student-id (vals (:students @a))))
  (ledger [_] (:ledger @a))
  (coordination-log [_] (:coordination-log @a))
  (commit-record! [_ record]
    (swap! a update :coordination-log conj record)
    record)
  (append-ledger! [_ fact] (swap! a update :ledger conj fact) fact)
  (with-students [s students] (when (seq students) (swap! a assoc :students students)) s))

(defn seed-db
  "A MemStore seeded with the demo student directory. The deterministic
  default."
  []
  (->MemStore (atom (assoc (demo-data) :ledger [] :coordination-log []))))

(defn mem-store
  "A MemStore seeded with an explicit `students` map (student-id string ->
  student map) -- the primary test/dev entry point. `students` may be empty
  (an unregistered-everywhere store)."
  [students]
  (->MemStore (atom (assoc {:students students}
                           :ledger []
                           :coordination-log []))))
