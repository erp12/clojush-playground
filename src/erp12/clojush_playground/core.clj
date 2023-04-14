(ns erp12.clojush-playground.core
  (:require [clojush.interpreter :refer [eval-push execute-instruction]])
  (:use
    ;; Ensure that all instructions defined in Clojush are registered.
    ;; @todo Make this dynamic based on what namespaces exist in the Clojush project.
    (clojush.instructions
      [boolean] [char] [code] [common] [environment] [genome] [gtm] [input-output] [numbers]
      [random-instructions] [string] [tag] [vectors] [zip])))


(defn print-state
  "Prints the full Push state. Differs from Clojush function because it only prints
  the fields that are present in the state, rather than all supported attributes."
  [state]
  (doseq [[typ stack] (sort-by first state)]
    (println typ "=" stack)))


(defn exercise-instruction-1
  "Performs 1 interpreter step and returns the next Push state.

  Args:
    - The instruction symbol
    - The Push state to apply the instruction to."
  [instruction before-state]
  (execute-instruction instruction before-state))


(defn exercise-instruction-full
  "Runs the instruction on the state and continues Push execution until termination.
  Returns the final state.

  Args:
    - The instruction symbol
    - The initial Push state to apply the instruction to.
    - A map of additional opts. Supported keys:
       :print-steps? - If true, prints all intermediate Push states during program execution."
  ([instruction before-state]
   (exercise-instruction-full instruction before-state {}))
  ([instruction before-state {:keys [print-steps?] :or {print-steps? false}}]
   (eval-push (update before-state :exec #(concat (list instruction) %))
              print-steps?)))
