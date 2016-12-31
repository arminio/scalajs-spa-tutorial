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
import spatutorial.client.components.{GlobalStyles, IdProvider, ReactTreeView, TreeItem}
import spatutorial.client.services._
import spatutorial.shared.Service

import scalacss.ScalaCssReact._

object TreeComp {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Services]])

  case class State()

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(LoadServices))

    def render(props: Props, s: State) =
      Panel(Panel.Props("All the things"), <.div(
        props.proxy().renderFailed(ex => "Error loading"),
        props.proxy().renderPending(_ > 5000, _ => "Loading..."),
        props.proxy().render { (services: Services) =>

          <.div(Tree(props, services))
        }))
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TreeComp")
    .initialState(State())
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
  @inline private def bss = GlobalStyles.bootstrapStyles


  case class State(content: String = "", services: Services, data: TreeItem)
  object State {
    def apply(services: Services): State = State(content = "", services = services, TreeItem(item = IdProvider(<.button("Loading"),"Loading.", "")))
  }
  case class Props(parentProps: TreeComp.Props, services: Services)

  class Backend($: BackendScope[Props, State]) {


    def initData = {

      $.modState { state =>
        def getChildren(s: Service) = s.functions.map(f => TreeItem(IdProvider(<.button(bss.buttonXS, bss.labelAsBadge, ^.id := f.id.str, f.name), f.id.str, searchString = s.serviceName + f.toString))) //!@ can this be generalized?

        println(s"=====> ${state.services.services}")
        val myData = TreeItem(IdProvider(<.button (bss.buttonPrimary, "Services"), "ROOT", "Services"), state.services.services.map(s => TreeItem(IdProvider(<.button(bss.buttonXS, ^.id := s.id.str, s.serviceName), s.id.str, searchString = s.toString), getChildren(s):_*)):_*)
        state.copy(data = myData)
      }
    }

    def itemSelectPF(p:Props, item: String, parent: String, depth: Int): Callback = {
//!@? this should result in rendering the selected item on the right:
      // p.parentProps.proxy.dispatchCB(LocTreeItemSelected(item)) >>
      itemSelectF(item,parent,depth)
    }

    def itemSelectF(item: String, parent: String, depth: Int): Callback = {
      val content =
        s"""Selected Item: $item <br>
           |Its Parent : $parent <br>
           |Its depth:  $depth <br>
        """.stripMargin




      Callback(dom.document.getElementById("treeviewcontent").innerHTML = content)
      //!@ dispatch? loc?
    }

    def render(p:Props, s:State) = {
      <.div(
        <.h3("Demo"),

//!@        ReactTreeView(
//          root = s.data,
////          root = data,
//          openByDefault = true,
//          onItemSelect = itemSelectPF(p , _:String, _:String, _:Int),
//          showSearchBox = true
//        ),
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
