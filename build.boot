; vim: syntax=clojure
(set-env! :dependencies (cond
  (= "1.8.0" (System/getenv "BOOT_CLOJURE_VERSION"))
    '[[org.clojure/clojure "1.8.0" :scope "provided"]]
  :else
    '[[org.clojure/clojure "1.9.0-alpha16" :scope "provided"]]))

(set-env!
  :resource-paths #{"src" "resources"}
  :source-paths #{"src"}
  :dependencies #(into % '[[org.clojure/clojurescript "1.9.542" :scope "test"]
                  [adzerk/boot-test        "1.2.0"     :scope "test"]
                  [adzerk/boot-cljs        "2.0.0"     :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]])
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}])
  )

(require '[adzerk.boot-test :as t]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
 pom {:project 'irresponsible/anarchy
      :version "0.2.1"
      :description "Logic without rules"}
 push {:tag true
       :ensure-branch "master"
       :ensure-release true
       :ensure-clean true
       :gpg-sign true
       :repo "clojars"}
 target {:dir #{"target"}})

(deftask testing []
  (set-env! :source-paths   #(conj % "test")
            :resource-paths #(conj % "test"))
  identity)

(deftask test []
  (testing)
  (t/test)
  (test-cljs))

(deftask autotest []
  (comp (testing) (watch) (test)))

;; RlsMgr Only stuff
(deftask release []
  (comp (pom) (jar) (push)))

(deftask jitpak-deploy []
  (task-options! pom {
    :project (symbol (System/getenv "ARTIFACT"))
  })
  (comp
    (pom)
    (jar)
    (target)      ; Must install to build dir
    (install)     ; And to .m2 https://jitpack.io/docs/BUILDING/#build-customization
  )
)
