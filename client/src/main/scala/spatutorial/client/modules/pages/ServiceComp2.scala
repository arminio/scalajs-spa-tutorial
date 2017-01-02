package spatutorial.client.modules.pages

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

import scala.scalajs.js
import scalacss.ScalaCssReact._

object ServiceComp2 {

  @inline private def bss = GlobalStyles.bootstrapStyles

  class Backend($: BackendScope[Props, _]) {
    def mounted(props: Props) =
    // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().services.isEmpty)(props.proxy.dispatchCB(LoadServices))


    def render(p: Props) = {
      val servicesPot = p.proxy().services

      Panel(Panel.Props("Service"), <.div(
        servicesPot.renderFailed(ex => "Error loading"),
        servicesPot.renderPending(_ > 5000, _ => "Loading..."),
        servicesPot.render { services => {

          val selectedItemId = p.proxy.value.selectedItemId
          //!@println(s"rendering ServiceComp2 selectedItemI: $selectedItemId")
          services.services.find(s => {

            s.id == selectedItemId
          }).map { s =>
            //!@println(s"rendering ServiceDetailsComp2 selectedItemI: $s")

            ServiceDetailsComp2(s, p.router, p.proxy)
          }
            //          services.services.find(s => s.id == p.serviceIdentifier).map(s => ServiceDetailsComp2(s, p.router, p.proxy))
            .fold(
              //None/empty case
              <.div(s"service with id: ${selectedItemId} (OR ${selectedItemId.str})  not found!")
            )(serviceDetailComp => <.div(serviceDetailComp))

        }

        }
      ))
    }

  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("Service")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(router: RouterCtl[Loc], proxy: ModelProxy[RootModel]) = component(Props(router, proxy))


  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[RootModel])

}

object ServiceDetailsComp2 {


  val component = ReactComponentB[Props]("ServiceDetailsComp")
    .initialState_P(p => State(p.service))
    .renderBackend[Backend]
    .componentWillReceiveProps(x => x.$.modState(_.copy(service = x.nextProps.service)))
    //    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(service: Service, router: RouterCtl[Loc], proxy: ModelProxy[RootModel]) = component(Props(service, router, proxy))

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(service: Service,
                   router: RouterCtl[Loc],
                   proxy: ModelProxy[RootModel])

  case class State(service: Service, editing: Boolean = false)

  class Backend($: BackendScope[Props, State]) {

    @inline private def bss = GlobalStyles.bootstrapStyles

    def editing =
      $.modState(s => s.copy(editing = !s.editing))

    def render(p: Props, state: State) = {
      val isEditing = state.editing
      val editButton = Button(Button.Props($.modState(state => state.copy(editing = !state.editing)), addStyles = Seq(bss.buttonPrimary)), if (isEditing) "Cancel" else "Edit")
      val saveButton = Button(Button.Props(save(p, state)  ), "Save")
      val backToServicesLink = Button(Button.Props( p.router.set(ServicesLoc), addStyles = Seq(bss.buttonDefault, bss.pullRight)), "Services")
      //      val backToServicesLink = <.a(bss.button, "Services", ^.onClick --> p.router.set(ServicesLoc))

      <.div(if (isEditing) edit(state.service) else view(state.service),
        editButton,
        if (isEditing) saveButton else EmptyTag,
        backToServicesLink
      )
    }

    def view(s: Service) = <.span(<.ul()(
      <.li(^.key := s"${s.id}-serviceName", s.serviceName),
      <.li(^.key := s"${s.id}-package", s.`package`),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def edit(s: Service) = <.span(bss.formGroup, <.ul()(
      <.li(^.key := s"${s.id}-serviceName", <.input.text(bss.formControl, ^.value := s.serviceName, ^.placeholder := "Service Name", ^.onChange ==> updateServiceName)),
      <.li(^.key := s"${s.id}-package", <.input.text(bss.formControl, ^.value := s.`package`, ^.placeholder := "Package filepath", ^.onChange ==> updatePackageFilepath)),
      <.li(^.key := s"${s.id}-provider", s.provider.toString)
    ))

    def updateServiceName(e: ReactEventI) = {
      val text = e.target.value
      //!@println(s"inputted servicename: $text")
      // update TodoItem content
      $.modState(state => state.copy(service = state.service.copy(serviceName = text)))
    }

    def updatePackageFilepath(e: ReactEventI) = {
      val text = e.target.value
      //!@println(s"inputted filepath: $text")
      // update TodoItem content
      $.modState(state => state.copy(service = state.service.copy(`package` = text)))
    }


    def save(props: Props, state: State) = {

      println(s"saving...")
      js.debugger()

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






