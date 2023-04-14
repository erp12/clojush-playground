# Clojush Playground

The Clojush project has been used as the PushGP implementation for multiple publications and has been used as the
reference implementation for multiple other PushGP systems. This project provides some simple utilities for exploring
some implementation details of Clojure which other practitioners who are building their own PushGP system should know.

Much of Clojush's development and usage patterns require a copy of the project's source code and knowledge of the 
project's build system. The playground provides utilities that only assume Clojush is on the class path and allows
users to specify a dependency on Clojush in whatever way they want (including whichever Clojush version).

## Getting Started

Many Clojush versions exist, including many private or one-off versions customized for a specific research effort. The
playground does not directly depend on Clojush so that user can specify the precise version they would like to explore.

Add the playground and you version of Clojush to your `deps.edn`. For this example we will use a version of Clojush from
clojars.

```clojure
:deps {
       clojush/clojush                    {:mvn/version "LATEST"
                                           ;; Clojush's dep on incanter is broken because the transitive dep bctsp-jdk14
                                           ;; is no longer accessible via maven central.
                                           :exclusions  [bouncycastle/bctsp-jdk14]}
       io.github.erp12/clojush-playground {:WRITE ME}
       }
```

Or simply start a REPL with these deps.

```commandline
clj -Sdeps '{:deps {clojush/clojush {:mvn/version "LATEST" :exclusions [bouncycastle/bctsp-jdk14]} }}'
```

Require the playground namespace in your REPL or script.

```clojure
(require '[erp12.clojush-playground.core :as play])
```

## Exercising Instructions

One difficult aspect of implementing a PushGP system is creating the many stack instructions. 
What separates the Push language from other non-turing complete stack machines is the use of an `exec` stack 
which holds the programs code and can be used to implement self-modifying logic for expression conditional control
flow, iteration, and other wacky stuff. To assist developers in the writing of `exec` stack instructions (and all
other instruction) the playground provides utility for running the Clojush instruction on handwritten states. If 
Clojush is your reference implementation, this can be used to generate unit tests for your PushGP implementation.

To perform a single step of program execution, use `exercise-instruction-1`.
The first argument is the instruction's symbol and the second is a Push state in the form of a 
map with keyword keys and the values are lists (aka the stack). The output will be the next
state map.

```clojure
(exercise-instruction
  'boolean_and
  {:boolean (list true false)})
;; => {:boolean (false)}

(exercise-instruction-1
  'exec_do*count
  {:integer (list 3)
   :exec    (list true)
   :boolean (list)})
;; {:integer (), :exec ((0 2 exec_do*range true)), :boolean ()}
```

To evaluate a Push instruction and continue program execution until termination, use the `exercise-instruction-all`.

```clojure
(exercise-instruction-full
    'exec_do*count
    {:integer (list 3)
     :exec    (list true)
     :boolean (list)})
;; {:integer (2 1 0), :exec (), :boolean (true true true), :termination :normal}
```

All the intermediate states can be printed using `:print-steps? true` in an map passed as the third argument.

```clojure
(exercise-instruction-full
    'exec_do*count
    {:integer (list 3)
     :exec    (list true)
     :boolean (list)}
    {:print-steps? true})
```