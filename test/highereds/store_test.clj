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
