import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "scalajs-spa"

  /** The version of your application */
  val version = "1.1.4"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.8"
//    val scala = "2.12.1"
    val scalaDom = "0.9.1"
    val scalajsReact = "0.11.3"
    val scalaCSS = "0.5.1"
    val log4js = "1.4.10"
    val autowire = "0.2.6"
    val booPickle = "1.2.5"
    val diode = "1.1.0"
    val uTest = "0.4.4"

    val react = "15.3.1"
    val jQuery = "1.11.1"
    val bootstrap = "3.3.6"
    val chartjs = "2.1.3"

    val scalajsScripts = "1.0.0"
    val monocleVersion = "1.3.2"
    val catsVersion = "0.9.0"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "me.chrons" %%% "boopickle" % versions.booPickle,
    "org.scalatest" %%% "scalatest" % "3.0.0" % "test",
//    "com.chuusai" %%% "shapeless" % "2.2.5" ,
    "org.typelevel" %%% "cats" % versions.catsVersion,
    "com.softwaremill.quicklens" %%% "quicklens" % "1.4.8",
    "com.github.julien-truffaut" %%%  "monocle-core"  % versions.monocleVersion,
    "com.github.julien-truffaut" %%%  "monocle-macro" % versions.monocleVersion,
    "com.github.julien-truffaut" %%%  "monocle-law"   % versions.monocleVersion % "test"
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
    "org.webjars" % "font-awesome" % "4.7.0" ,
//    "org.webjars" % "font-awesome" % "4.3.0-1" % Provided,
    "org.webjars" % "bootstrap" % versions.bootstrap % Provided,
    "com.amazonaws" % "aws-java-sdk" % "1.11.73",
//    "org.yaml" % "snakeyaml" % "1.17",
    "net.jcazevedo" %% "moultingyaml" % "0.4.0",
    "com.lihaoyi" %% "ammonite-ops" % "0.8.1",
    "com.lihaoyi" %% "utest" % versions.uTest % Test
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "me.chrons" %%% "diode" % versions.diode,
    "me.chrons" %%% "diode-react" % versions.diode,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test
//    ,
//    "com.github.japgolly.fork.monocle" %%% "monocle-core" % "1.2.0",
//
//      "com.github.japgolly.fork.monocle" %%% "monocle-generic" % "1.2.0",
//
//  "com.github.japgolly.fork.monocle" %%% "monocle-state" % "1.2.0",
//
//  "com.github.japgolly.fork.monocle" %%% "monocle-macro" % "1.2.0"
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
    "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
    "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js",
    "org.webjars" % "bootstrap" % versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
    "org.webjars" % "chartjs" % versions.chartjs / "Chart.js" minified "Chart.min.js",
    "org.webjars" % "log4javascript" % versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
  ))
}
