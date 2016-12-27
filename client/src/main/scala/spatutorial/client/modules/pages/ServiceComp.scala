package spatutorial.client.modules.pages

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^.{<, _}
import spatutorial.client.SPAMain.{Loc, ServicesLoc}
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.GlobalStyles
import spatutorial.client.services._
import spatutorial.shared._


object ServiceComp {

  // create the React component for To Do management
  val component = ReactComponentB[Props]("Service")
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
          services.services.find(s => s.id == p.serviceIdentifier).map(s => ServiceDetailsComp(s, p.router, p.proxy))
            .fold(
              //None/empty case
              <.div(s"service with id: ${p.serviceIdentifier} (OR ${p.serviceIdentifier.str})  not found!")
            )(serviceDetailComp => <.div(serviceDetailComp))

        }

        }
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
        if (state.editing) <.button("Save", ^.disabled := !state.editing,  ^.onClick --> save(p, state)) else EmptyTag,
        <.a("Services", ^.onClick --> p.router.set(ServicesLoc))
      )
    }

    def view(s: Service) = <.span(<.ul()(
      <.li(^.key := s"${s.id}-serviceName", s.serviceName),
      <.li(^.key := s"${s.id}-package", s.`package`),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def edit(s: Service) = <.span(<.ul()(
      <.li(^.key := s"${s.id}-serviceName", <.input.text(^.value := s.serviceName, ^.placeholder := "Service Name", ^.onChange ==> updateServiceName)),
      <.li(^.key := s"${s.id}-package", <.input.text(^.value := s.`package`, ^.placeholder := "Package filepath", ^.onChange ==> updatePackageFilepath)),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def updateServiceName(e: ReactEventI) = {
      val text = e.target.value
      println(s"inputted servicename: $text")
      // update TodoItem content
      $.modState(state => state.copy(service = state.service.copy(serviceName = text)))
    }

    def updatePackageFilepath(e: ReactEventI) = {
      val text = e.target.value
      println(s"inputted filepath: $text")
      // update TodoItem content
      $.modState(state => state.copy(service = state.service.copy(`package` = text)))
    }


    def save(props: Props, state: State) = {

      println(s"saving...")

//      props.proxy.dispatchCB(SaveService(state.service)) >> props.router.set(ServicesLoc)
      props.proxy.dispatchCB(SaveService(state.service)) >> $.modState(s => s.copy(editing = !s.editing))

    }

    //  private val ServiceDetails = ReactComponentB[Props]("ServiceDetailsComp")
    //    .render_P(p => {
    //      val style = bss.listGroup
    //      <.div(s"it's working! ${p.service}")
    //    })
    //    .build
  }
}




