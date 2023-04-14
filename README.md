# Clojush Playground

The Clojush project has been used as the PushGP implementation for many publications and as the
reference implementation for multiple other PushGP systems. This project provides some utilities for
exploring certain implementation details of Clojush which practitioners building their own PushGP 
system might find useful.

Much of Clojush's development and usage requires a copy of the project's source code and knowledge of the 
build system. The playground provides utilities that only assume Clojush is on the class path and allows
users to specify a dependency on Clojush in whatever way they want (including whichever Clojush version).
This is particularly useful for starting the playground quickly in a REPL without much fuss (see below).

## Getting Started

Many Clojush versions exist, including many private or one-off versions customized for a specific research effort. The
playground does not directly depend on Clojush so you can specify the precise version you would like to explore.

Add the playground and version of Clojush to your `deps.edn`. For this example we will use the lastest version of 
Clojush published to Clojars.

```clojure
:deps {
       clojush/clojush                    {:mvn/version "LATEST"
                                           ;; Clojush's dep on incanter is broken because the transitive dep bctsp-jdk14
                                           ;; is no longer accessible via maven central.
                                           :exclusions  [bouncycastle/bctsp-jdk14]}
       io.github.erp12/clojush-playground {:git/sha "72deacfe157da2134878ed4702a861e1ebc8bcda"}
       }
```

Or simply start a REPL with this 1 line command.

```commandline
clj -Sdeps '{:deps {clojush/clojush {:mvn/version "LATEST" :exclusions [bouncycastle/bctsp-jdk14]} io.github.erp12/clojush-playground {:git/sha "72deacfe157da2134878ed4702a861e1ebc8bcda"}}}'
```

Require the playground namespace in your REPL or script.

```clojure
(require '[erp12.clojush-playground.core :as play])
```

## Exercising Instructions

One difficult aspect of implementing a PushGP system is creating the many stack instructions. 
A key feature of the Push langauge is the `exec` stack which holds the programs code and is modified by 
instructions to express conditional control flow, iteration, and other wacky self-modifying logic.

To assist developers in the writing of the common `exec` stack instructions (or any Clojush instruction)
the playground provides utilities for running the Clojush instruction on arbirary Push states. If 
Clojush is your reference implementation, this can be used to generate unit tests for your PushGP
implementation.

To perform a single step of program execution, use `exercise-instruction-1`.
The first argument is the instruction's symbol and the second is a Push state in the form of a 
map with keyword keys and the values are lists (aka the stack). The output will be the next
state map.

```clojure
(play/exercise-instruction-1
  'boolean_and
  {:boolean (list true false)})
;; => {:boolean (false)}

(play/exercise-instruction-1
  'exec_do*count
  {:integer (list 3)
   :exec    (list true)
   :boolean (list)})
;; {:integer (), :exec ((0 2 exec_do*range true)), :boolean ()}
```

To evaluate a Push instruction and continue program execution until termination, use the `exercise-instruction-all`.

```clojure
(play/exercise-instruction-full
    'exec_do*count
    {:integer (list 3)
     :exec    (list true)
     :boolean (list)})
;; {:integer (2 1 0), :exec (), :boolean (true true true), :termination :normal}
```

All the intermediate states can be printed using `:print-steps? true` in an map passed as the third argument.

```clojure
(play/exercise-instruction-full
    'exec_do*count
    {:integer (list 3)
     :exec    (list true)
     :boolean (list)}
    {:print-steps? true})
```
