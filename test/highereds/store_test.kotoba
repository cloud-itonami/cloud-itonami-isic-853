(ns highereds.store-test
  (:require [clojure.test :as t]
            [highereds.store :as store]))

(t/deftest seed-db-creates-default-store
  (let [s (store/seed-db)]
    (t/is (not (nil? s)))
    (t/is (seq (store/all-students s)))))

(t/deftest student-lookup
  (let [s (store/seed-db)
        student (store/student s "student-1")]
    (t/is (= "student-1" (:student-id student)))
    (t/is (= "Aiko Yamada" (:name student)))
    (t/is (:registered? student))
    (t/is (:verified? student))))

(t/deftest student-not-found
  (let [s (store/seed-db)
        student (store/student s "student-999")]
    (t/is (nil? student))))

(t/deftest unverified-student
  (let [s (store/seed-db)
        student (store/student s "student-3")]
    (t/is (= "student-3" (:student-id student)))
    (t/is (:registered? student))
    (t/is (not (:verified? student)))))

(t/deftest all-students
  (let [s (store/seed-db)
        students (store/all-students s)]
    (t/is (= 3 (count students)))
    (t/is (= ["student-1" "student-2" "student-3"]
             (map :student-id students)))))

(t/deftest append-ledger
  (let [s (store/seed-db)]
    (t/is (empty? (store/ledger s)))
    (store/append-ledger! s {:op :test :timestamp 123})
    (t/is (= 1 (count (store/ledger s))))
    (t/is (= :test (:op (first (store/ledger s)))))))

(t/deftest mem-store-with-custom-students
  (let [custom-students {"student-x" {:student-id "student-x" :name "Test Student"
                                       :enrollment-status "active"
                                       :registered? true :verified? true}}
        s (store/mem-store custom-students)
        student (store/student s "student-x")]
    (t/is (= "student-x" (:student-id student)))
    (t/is (= "Test Student" (:name student)))))

(t/deftest empty-store
  (let [s (store/mem-store {})
        students (store/all-students s)]
    (t/is (empty? students))
    (t/is (nil? (store/student s "any-id")))))

;; ----------------------------- backend parity (MemStore vs DatomicStore) -----------------------------
;; Proves `DatomicStore` satisfies the SAME `Store` protocol contract as `MemStore` --
;; the same pattern `cloud-itonami-isic-7810`'s `employmentops.store-contract-test` uses.

(defn- backends []
  [["MemStore" (store/seed-db)] ["DatomicStore" (store/datomic-seed-db)]])

(t/deftest read-parity
  (doseq [[label s] (backends)]
    (t/testing label
      (t/is (= "Aiko Yamada" (:name (store/student s "student-1"))))
      (t/is (true? (:registered? (store/student s "student-1"))))
      (t/is (true? (:verified? (store/student s "student-1"))))
      (t/is (false? (:verified? (store/student s "student-3"))) "student-3 is registered but unverified")
      (t/is (nil? (store/student s "no-such-student")))
      (t/is (= ["student-1" "student-2" "student-3"] (mapv :student-id (store/all-students s))))
      (t/is (= [] (store/ledger s)))
      (t/is (= [] (store/coordination-log s))))))

(t/deftest write-and-ledger-parity
  (doseq [[label s] (backends)]
    (t/testing label
      (t/testing "commit-record! appends to coordination-log"
        (store/commit-record! s {:op :log-attendance-note :student-id "student-1" :value {:meal "lunch"}})
        (t/is (= 1 (count (store/coordination-log s))))
        (t/is (= "student-1" (:student-id (first (store/coordination-log s))))))
      (t/testing "append-ledger! is append-only and order-preserving"
        (store/append-ledger! s {:op :a :disposition :commit})
        (store/append-ledger! s {:op :b :disposition :hold})
        (t/is (= [:commit :hold] (mapv :disposition (store/ledger s))))))))

(t/deftest datomic-empty-store-is-usable
  (let [s (store/datomic-store)]
    (t/is (nil? (store/student s "nope")))
    (t/is (= [] (store/all-students s)))
    (t/is (= [] (store/ledger s)))
    (t/is (= [] (store/coordination-log s)))
    (store/with-students s {"x" {:student-id "x" :name "New Student" :enrollment-status "active"
                                 :registered? true :verified? false}})
    (t/is (= "New Student" (:name (store/student s "x"))))))
