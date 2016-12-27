package spatutorial.client.modules.pages

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react.CallbackTo.MapGuard
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.ReactTagOf
import japgolly.scalajs.react.vdom.prefix_<^.{<, _}
import org.scalajs.dom.html.Span
import spatutorial.client.SPAMain.{FunctionsLoc, Loc, ServiceLoc, ServicesLoc}
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.GlobalStyles
import spatutorial.client.components.TodoList.TodoListProps
import spatutorial.client.modules.pages.ServiceDetailsComp.Props
import spatutorial.client.services._
import spatutorial.shared._


object ServiceComp {

  // create the React component for To Do management
  val component = ReactComponentB[Props]("Service")
    //    .initialState {
    //      State()
    //    }
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc], identifier: Identifier, proxy: ModelProxy[Pot[Services]]) = component(Props(router, identifier, proxy))

  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], serviceIdentifier: Identifier, proxy: ModelProxy[Pot[Services]])

  class Backend($: BackendScope[Props, _]) {
    def mounted(props: Props) =
    // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(LoadServices))


    def render(p: Props) = {
      val proxy = p.proxy()

      Panel(Panel.Props("Service"), <.div(
        proxy.renderFailed(ex => "Error loading"),
        proxy.renderPending(_ > 5000, _ => "Loading..."),
        proxy.render { services => {
          services.services.find(s => s.id == p.serviceIdentifier).map(s => ServiceDetailsComp(s, p.router, p.proxy)) match {
            case None =>
              val msg = s"service with id: ${p.serviceIdentifier} (OR ${p.serviceIdentifier.str})  not found!"
              println(msg)
              //!@            val span: ReactTagOf[Span] = <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, "!!")

              <.div(msg)

            case Some(y) => y
          }

        }

        }
        //        proxy.render { (services: Services) => {
        //          //          val service = services.services.find(_.id == p.serviceIdentifier)
        //          state.service.fold {
        //            val msg = s"service with id: ${p.serviceIdentifier} (OR ${p.serviceIdentifier.str})  not found!"
        //            println(msg)
        //            //!@            val span: ReactTagOf[Span] = <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, "!!")
        //
        //            <.div(msg)
        //
        //          } { (s: Service) =>
        //
        //            <.div(
        //              if (state.editing) {
        //                edit(s)
        //              } else {
        //                view(s)
        //              },
        //              <.button(if (state.editing) "Cancel" else "Edit", ^.onClick --> $.modState(state => state.copy(editing = !state.editing)))
        //            )
        //          }
        //
        //        }
        //        }
      ))
    }

  }
}


object ServiceDetailsComp {
  val component = ReactComponentB[Props]("ServiceDetailsComp")
    .initialState_P(p => State(p.service))
    .renderBackend[Backend]
    //    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(service: Service, router: RouterCtl[Loc], proxy: ModelProxy[Pot[Services]]) = component(Props(service, router, proxy))

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(service: Service, router: RouterCtl[Loc], proxy: ModelProxy[Pot[Services]])

  case class State(service: Service, editing: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def editing =
      $.modState(s => s.copy(editing = !s.editing))

    def render(p: Props, state: State) = {
      <.div(if (state.editing) edit(state.service) else view(state.service),
        <.button(if (state.editing) "Cancel" else "Edit", ^.onClick --> $.modState(state => state.copy(editing = !state.editing))),
        <.button("Save", ^.onClick --> save(p, state))
      )
    }

    def view(s: Service) = <.span(<.ul()(
      <.li(^.key := s"${s.id}-serviceName", s.serviceName),
      <.li(^.key := s"${s.id}-package", s.`package`),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def edit(s: Service) = <.span(<.ul()(
      <.li(^.key := s"${s.id}-serviceName", <.input.text(^.value := s.serviceName, ^.placeholder := "Service Name", ^.onChange ==> updateServiceName)),
      <.li(^.key := s"${s.id}-package", s.`package`),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def updateServiceName(e: ReactEventI) = {
      val text = e.target.value
      println(s"inputed: $text")
      // update TodoItem content
      $.modState(state => state.copy(service = state.service.copy(serviceName = text)))

    }
    def save(props: Props, state: State) = {

      println(s"saving...")
      // update TodoItem content
      props.proxy.dispatchCB(SaveService(state.service)) >> props.router.set(ServicesLoc)

    }

    //  private val ServiceDetails = ReactComponentB[Props]("ServiceDetailsComp")
    //    .render_P(p => {
    //      val style = bss.listGroup
    //      <.div(s"it's working! ${p.service}")
    //    })
    //    .build
  }
}




