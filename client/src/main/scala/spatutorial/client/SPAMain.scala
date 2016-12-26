package spatutorial.client

import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom
import spatutorial.client.components.GlobalStyles
import spatutorial.client.logger._
import spatutorial.client.modules._
import spatutorial.client.services.SPACircuit

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc
  case object FuncLoc extends Loc
  case object InvoiceLoc extends Loc

  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    val todoWrapper = SPACircuit.connect(_.todos)

    val invoiceWrapper = SPACircuit.connect(rootModel => rootModel.invoices)

    // wrap/connect components to the circuit
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap(_.motd)(proxy => Dashboard(ctl, proxy)))
      | staticRoute("#todo", TodoLoc) ~> renderR(ctl => todoWrapper(Todo(_)))
      | staticRoute("#invoice", InvoiceLoc) ~> renderR(ctl => invoiceWrapper(Invoice(_)))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  val todoCountWrapper = SPACircuit.connect(model => (model.todos.map(_.items.count(!_.completed)).toOption, model.invoices.map(_.items.count(!_.completed)).toOption))
  val invoiceCountWrapper = SPACircuit.connect(_.invoices.map(_.items.count(!_.completed)).toOption)
  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
          <.div(^.className := "collapse navbar-collapse",
            // connect menu to model, because it needs to update when the number of open todos changes
            todoCountWrapper((proxy: ModelProxy[(Option[Int], Option[Int])]) => MainMenu(c, r.page, proxy))

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
