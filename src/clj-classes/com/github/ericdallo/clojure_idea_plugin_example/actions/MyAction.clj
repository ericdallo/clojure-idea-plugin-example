(ns com.github.ericdallo.clojure-idea-plugin-example.actions.MyAction
  (:import
   (com.intellij.ide
    BrowserUtil))
  (:gen-class
   :extends com.intellij.openapi.actionSystem.AnAction))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn -actionPerformed [_this _e]
  (BrowserUtil/launchBrowser "https://stackoverflow.com/questions/ask"))
