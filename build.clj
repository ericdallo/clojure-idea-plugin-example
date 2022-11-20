(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'com.github.ericdallo/clojure_idea_plugin_example)

(def version "0.0.1")
(def class-dir "target/classes")
(def clj-class-dir "target/clj-classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def plugin-file (format "target/%s-%s-plugin.jar" (name lib) version))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn clj-classes [_]
  (b/copy-dir {:src-dirs ["src/clj-classes"]
               :target-dir clj-class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj-classes"]
                  :class-dir clj-class-dir}))

(defn jar [_]
  (clean nil)
  (clj-classes nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src/main" "src/clj-classes"]})
  (b/copy-dir {:src-dirs ["src/main" "src/clj-classes" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn install [_]
  (jar {})
  (b/install {:basis basis
              :lib lib
              :version version
              :jar-file jar-file
              :class-dir class-dir}))

(defn plugin-jar [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["resources"]
               :target-dir class-dir})
  (b/compile-clj {:src-dirs ["src/main" "src/clj-classes"]
                  :class-dir class-dir
                  :basis basis})
  (b/uber {:class-dir class-dir
           :uber-file plugin-file
           :main 'com.github.ericdallo.clojure-idea-plugin-example.main
           ;; :exclude [#"^(?!.*?clojure|.*clojure_idea_plugin_example).*"]
           :basis basis}))

(defn deploy [opts]
  (jar opts)
  ((requiring-resolve 'deps-deploy.deps-deploy/deploy)
   (merge {:installer :remote
           :artifact jar-file
           :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
          opts))
  opts)
