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

  case class Props(router: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]])

  private case class MenuItem(idx: Int, label: (Props) => ReactNode, icon: Icon, location: Loc)


  private def buildServiceMenu(props: Props): ReactElement = {
    val proxy = props.proxy()
    //!@println(proxy)
    val pageXXCount = proxy.getOrElse(0)

    <.span(
      <.span("Services "), pageXXCount > 0 ?= <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, pageXXCount)
    )
  }
  private def buildNewServiceMenu(props: Props): ReactElement = {

    <.span(
      <.span("New Service")
    )
  }

  def newIndex = {
  var index = 0
    index = index + 1
    index
  }

  private val menuItems = Seq(
    MenuItem(1, buildServiceMenu, Icon.dashboard, TreeLoc),
    MenuItem(2, buildNewServiceMenu, Icon.dashboard, NewServiceLoc)
//    MenuItem(1, _ => "Dashboard", Icon.dashboard, DashboardLoc),
//    MenuItem(2, buildTodoMenu, Icon.check, TodoLoc),
//    MenuItem(3, buildPageXXMenu, Icon.check, PageXXLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def mounted(props: Props) = {
      // dispatch a message to refresh the todos
      val empty = props.proxy.value.isEmpty
      //!@println(empty)
//      js.debugger()
      Callback.when(empty)(props.proxy.dispatchCB(LoadServices))
    }

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

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]]): ReactElement =
    component(Props(ctl, currentLoc, proxy))
}
