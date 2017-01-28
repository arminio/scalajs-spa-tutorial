package spatutorial.client.modules.pages

import diode.UseValueEq
import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^.{<, TagMod, _}
import org.scalajs.dom
import spatutorial.client.SPAMain.{FunctionsLoc, Loc, ServiceLoc}
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.{GlobalStyles, IdProvider, ReactTreeView, TreeItem}
import spatutorial.client.services._

import scala.scalajs.js
import scalacss.ScalaCssReact._

object TreeComp {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[RootModel], children: ReactNode*)

  case class State(treeRootWrapper: ReactConnectProxy[TreeItem])

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().services.isEmpty)(props.proxy.dispatchCB(LoadServices))

    def render(props: Props, s: State) =  {
      val potSservices = props.proxy().services
      <.div(
        potSservices.renderFailed(ex => "Error loading"),
        potSservices.renderPending(_ > 5000, _ => "Loading..."),
        potSservices.render { services =>
          <.div(^.className := "container-fluid",
            <.div(^.className := "row",
//              do like this (from Dashboard code of SPA):
//          .initialState_P(props => State(props.proxy.connect((m: Pot[String]) => m)))
              <.div(^.className := "pull-left col-sm-3", s.treeRootWrapper(modelProxy => Tree(props.router, modelProxy))),
              <.div(^.className := "pull-left col-sm-9", props.children)
            )
          )
        }
      )
    }
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TreeComp")
//    .initialState_P(props => State(props.proxy.connect((m: Pot[String]) => m)))
    .initialState_P(props => State(props.proxy.connect(_.treeRoot)))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc], proxy: ModelProxy[RootModel], children: ReactNode*) = component(Props(router, proxy, children:_*))
}



////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
////////////// TREE
object Tree {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[TreeItem])

  case class State(content: String = "", rootTreeItem: TreeItem, treeRootWrapper: ReactConnectProxy[TreeItem])

  object State {
    //!@ remove services
    def apply(treeRootWrapper: ReactConnectProxy[TreeItem]): State
    = State(content = "", TreeItem(item = IdProvider(<.button("Loading"),"Loading.", "")), treeRootWrapper)
  }

  class Backend($: BackendScope[Props, State]) {

//    //!@ remove this as TreeItems are given to us now
//    def initData = {
//
//      $.modState { state =>
//
//        state.copy(rootTreeItem = convertToTreeItems(state.services))
//      }
//    }

//    //!@ remove this as TreeItems are given to us now
//    def convertToTreeItems(services: Services) : TreeItem = {
//      def getChildren(s: Service) = s.functions.map(f => TreeItem(IdProvider(<.button(bss.buttonXS, bss.labelAsBadge, ^.id := f.id.str, f.name), f.id.str, searchString = s.serviceName + f.toString))) //!@ can this be generalized?
//
//      //!@println(s"=====> ${services}")
//      TreeItem(IdProvider(<.button (bss.buttonPrimary, "Services"), "ROOT", "Services"), services.services.map(s => TreeItem(IdProvider(<.button(bss.buttonXS, ^.id := s.id.str, s.serviceName), s.id.str, searchString = s.toString), getChildren(s):_*)):_*)
//    }

    def itemSelectPF(p:Props, item: String, parent: String, depth: Int): Callback = {
//!@? this should result in rendering the selected item on the right:
      //!@println(s"tree item selected and selected id update trigger: $item")
      p.proxy.dispatchCB(LocTreeItemSelected(item)) >>
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

//      //!@println(s"Tree's S services: ${s.services}")
//      //!@println(s"Tree's P services: ${p.services}")
//      //!@println(s"Tree's S Data: ${s.data.children}")
//      js.debugger

      <.div(

        <.div(
          <.h3("Demo"), <.button("New Service"),
          s.treeRootWrapper((modelProxy: ModelProxy[TreeItem]) => {

            ReactTreeView(
              modelProxy,
              openByDefault = true,
              onItemSelect = itemSelectPF(p, _: String, _: String, _: Int),
              showSearchBox = true
            )

          }
        ) ,
          <.strong(^.id := "treeviewcontent")  )
      )



    }
  }

  val component = ReactComponentB[Props]("ReactTreeViewDemo")
    .initialState_P { (p: Props) =>
      val connect = p.proxy.connect(t => t)
      State(connect)
    }
    .renderBackend[Backend]
//      .componentDidMount(scope => scope.backend.initData)
//    .componentWillReceiveProps { case ComponentWillReceiveProps(_$, newProps) =>
//      _$.modState {
//
//        _.copy(rootTreeItem = TreeItem(IdProvider(<.div("this chicken"), "", "")))
//      }
//    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[TreeItem]) = component(Props(router, proxy))
}
