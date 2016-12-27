package spatutorial.client

import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.StaticDsl.Route
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spatutorial.client.components.GlobalStyles
import spatutorial.client.logger._
import spatutorial.client.modules._
import spatutorial.client.services.{SPACircuit, Services}
import spatutorial.shared.{Function, Service}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object ServicesLoc extends Loc
  case object FunctionsLoc extends Loc
  case class ServiceLoc(id: String) extends Loc
  case class FunctionLoc(id: String) extends Loc

  case object ErrorLoc extends Loc
//  case object DashboardLoc extends Loc
//  case object TodoLoc extends Loc
//  case object PageXXLoc extends Loc


//  case class ServiceId(id: String) extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    val servicesWrapper = SPACircuit.connect(_.services)
    val functionsWrapper = SPACircuit.connect(_.services.get.services.flatMap(service => service.functions))

    // wrap/connect components to the circuit
    (
      staticRoute("#error", ErrorLoc) ~>  render( <.h1("Errored!!!!") ) |
      staticRoute("#services", ServicesLoc) ~> renderR(ctl => servicesWrapper((props: ModelProxy[Pot[Services]]) => ServicesComp(ctl, props))) |
      staticRoute("#functions", FunctionsLoc) ~> renderR(ctl => functionsWrapper((props: ModelProxy[Seq[Function]]) => FunctionsComp(ctl, props))) |
      dynamicRouteCT("#service" / string("[a-zA-Z0-9]+").caseClass[ServiceLoc]) ~> dynRender(x => <.h1(s"Service ${x.asInstanceOf[ServiceLoc].id}!!!!")) |
      dynamicRouteCT("#function" / string("[a-zA-Z0-9]+").caseClass[FunctionLoc]) ~> dynRender(x => <.h1(s"Function ${x.asInstanceOf[FunctionLoc].id}!!!!"))
//        |
//      dynamicRoute(r) ~> dynRender(x => <.h1(s"Chicken ${x}!!!!") )
      ).notFound(redirectToPage(ErrorLoc)(Redirect.Replace))

  }.renderWith(layout)


  val serviceCounterWrapper = SPACircuit.connect(model => (model.services.map(_.services.size)).toOption)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
          <.div(^.className := "collapse navbar-collapse",
            // connect menu to model, because it needs to update when the number of open todos changes
            serviceCounterWrapper((proxy: ModelProxy[Option[Int]]) => MainMenu(c, r.page, proxy)) //!@ remove this

          )
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
