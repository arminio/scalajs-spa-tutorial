package spatutorial.client.modules

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.SPAMain._
import spatutorial.client.components.Bootstrap.CommonStyle
import spatutorial.client.components.Icon._
import spatutorial.client.components._
import spatutorial.client.services._

import scala.scalajs.js
import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[(Option[Int], Option[Int])])

  private case class MenuItem(idx: Int, label: (Props) => ReactNode, icon: Icon, location: Loc)

  // build the Todo menu item, showing the number of open todos
  private def buildTodoMenu(props: Props): ReactElement = {
    val todoCount = props.proxy()._1.getOrElse(0)
    <.span(
      <.span("Todo "),
      todoCount > 0 ?= <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, todoCount)
    )
  }

  // build the Invoice menu item, showing the number of open invoices
  private def buildInvoiceMenu(props: Props): ReactElement = {
    val proxy = props.proxy()
    println(proxy)
    val invoiceCount = proxy._2.getOrElse(0)
    js.debugger()
    <.span(
      <.span("Invoice "),
      invoiceCount > 0 ?= <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, invoiceCount)
    )
  }

  def newIndex = {
  var index = 0
    index = index + 1
    index
  }

  private val menuItems = Seq(
    MenuItem(1, _ => "Dashboard", Icon.dashboard, DashboardLoc),
    MenuItem(2, buildTodoMenu, Icon.check, TodoLoc),
    MenuItem(3, buildInvoiceMenu, Icon.check, InvoiceLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos
      Callback.when(props.proxy.value._1.isEmpty)(props.proxy.dispatchCB(RefreshTodos))

    def render(props: Props) = {
      <.ul(bss.navbar)(
        // build a list of menu items
        for (item <- menuItems) yield {
          <.li(^.key := item.idx, (props.currentLoc == item.location) ?= (^.className := "active"),
            props.router.link(item.location)(item.icon, " ", item.label(props))
          )
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("MainMenu")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[(Option[Int], Option[Int])]): ReactElement =
    component(Props(ctl, currentLoc, proxy))
}
