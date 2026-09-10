# cloud-itonami-isic-853

**Higher Education** — ISIC Rev.4 class 853.

A coordination-only actor for higher-education institutions (universities, colleges), behind an independent Governor that earns advisor trust through structured oversight: proposal → advise → govern → decide → commit|hold|escalate.

## Features

- **Closed proposal-op allowlist**: schedule-enrollment-appointment, coordinate-facility-booking, coordinate-supply-request, schedule-staff-shift-proposal, flag-safety-concern (all `:effect :propose`).
- **Three HARD governor checks** (permanent, un-overridable):
  1. **Student verified** — target must exist AND be registered/verified in the store.
  2. **Effect is :propose** — any other `:effect` value is rejected.
  3. **Scope exclusion** — academic grading/assessment, admissions decisions, academic standing/probation/expulsion decisions, degree-conferral decisions, disciplinary action, and safety-authority overrides are permanently blocked.
- **Staged rollout** (Phase 0→3):
  - Phase 0: read-only
  - Phase 1: enrollment-appointment scheduling only (approval-gated)
  - Phase 2: + facility booking, supply, shift proposals (approval-gated)
  - Phase 3: auto-commits clean proposals (safety concerns always escalate)
- **Append-only audit ledger** — every decision is an immutable log entry.
- **langgraph-clj StateGraph** — one request = one supervised run; human-in-the-loop via `interrupt-before`.

## Development

```bash
# Install dependencies
clojure -M:dev -P

# Run tests
clojure -M:dev:test

# Run linter
clojure -M:lint

# Run demo
clojure -M:run
```

## Test suite

- `test/highereds/governor_test.kotoba` — unit tests of governor hard checks and scope exclusion
- `test/highereds/advisor_test.kotoba` — advisor proposal shape and consistency
- `test/highereds/phase_test.kotoba` — rollout phase logic
- `test/highereds/governor_contract_test.clj` — full graph integration, audit trail
- `test/highereds/store_contract_test.clj` — Store protocol and MemStore implementation

## Modules

- `highereds.store` — SSoT (MemStore, String-keyed student directory, append-only ledger)
- `highereds.advisor` — contained intelligence node (mock + real-LLM seam)
- `highereds.governor` — independent compliance layer
- `highereds.operation` — closed proposal-op allowlist
- `highereds.phase` — staged rollout (Phase 0→3)
- `highereds.sim` — demo/test harness
