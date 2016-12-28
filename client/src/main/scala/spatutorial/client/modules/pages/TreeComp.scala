package spatutorial.client.modules.pages

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^.{<, _}
import org.scalajs.dom
import spatutorial.client.SPAMain.{FunctionsLoc, Loc, ServiceLoc}
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.{ReactTreeView, TreeItem}
import spatutorial.client.services._
import spatutorial.shared.Service

object TreeComp {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Services]])

  case class State()

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(LoadServices))

    def render(p: Props, s: State) =
      Panel(Panel.Props("All the things"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 5000, _ => "Loading..."),
        p.proxy().render { (services: Services) =>

          <.div(Tree(p, services))
        }))
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TODO")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Services]]) = component(Props(router, proxy))
}



////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
object Tree {


  case class State(content: String = "", services: Services, data: TreeItem)
  object State {
    def apply(services: Services): State = State(content = "", services = services, TreeItem(item = "Loading"))
  }
  case class Props(parentProps: TreeComp.Props, services: Services)

  class Backend(t: BackendScope[Props, State]) {


    def initData = {
        val data: TreeItem = TreeItem("root",
          TreeItem("dude1",
            TreeItem("dude1c")),
          TreeItem("dude2"),
          TreeItem("dude3"),
          TreeItem("dude4",
            TreeItem("dude4c",
              TreeItem("dude4cc")))
        )

      t.modState { state =>
        def getChildren(s: Service) = s.functions.map(f => TreeItem(f.name))

        val myData = TreeItem("Services", state.services.services.map(s => TreeItem(s.serviceName, getChildren(s):_*)):_*)
        state.copy(data = myData)
      }
    }

    def itemSelectF(item: String, parent: String, depth: Int): Callback = {
      val content =
        s"""Selected Item: $item <br>
           |Its Parent : $parent <br>
           |Its depth:  $depth <br>
        """.stripMargin
      Callback(dom.document.getElementById("treeviewcontent").innerHTML = content)
    }

    def render(p:Props, s:State) = {
      <.div(
        <.h3("Demo"),

        ReactTreeView(
          root = s.data,
//          root = data,
          openByDefault = true,
          onItemSelect = itemSelectF _,
          showSearchBox = true
        ),
        <.strong(^.id := "treeviewcontent")
      )

    }
  }

  val component = ReactComponentB[Props]("ReactTreeViewDemo")
    .initialState_P((p: Props) => State(p.services))
    .renderBackend[Backend]
      .componentDidMount(scope => scope.backend.initData)
    .build

  def apply(p: TreeComp.Props, services: Services) = component(Props(p, services))
}
