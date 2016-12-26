package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components._
import spatutorial.client.logger._
import spatutorial.client.services._
import spatutorial.shared._

import scalacss.ScalaCssReact._

object Function {

  case class Props(proxy: ModelProxy[Pot[Function]])

  case class State(selectedItem: Option[FunctionParameters] = None, showFunctionForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the Functions, which will cause FunctionStore to fetch Functions from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshFunctions))

    def editFunction(item: Option[FunctionParameters]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showFunctionForm = true))

    def FunctionEdited(item: FunctionParameters, cancelled: Boolean): CallbackTo[Unit] = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Function editing cancelled")
      } else {
        Callback.log(s"Function edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateFunction(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showFunctionForm = false))
    }

    def render(p: Props, s: State) =
      Panel(Panel.Props("##Title##"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 5000, _ => "Loading..."),
        p.proxy().render(Functions => FunctionList(Functions.parameters, item => p.proxy.dispatchCB(UpdateFunction(item)),
          item => editFunction(Some(item)), item => p.proxy.dispatchCB(DeleteFunction(item)))),
        Button(Button.Props(editFunction(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showFunctionForm) FunctionForm(FunctionForm.Props(s.selectedItem, FunctionEdited))
        else // otherwise add an empty placeholder
          Seq.empty[ReactElement])
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("Function")
    .initialState(State()) // initial state from FunctionStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Function]]) = component(Props(proxy))
}

object FunctionForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[FunctionParameters], submitHandler: (FunctionParameters, Boolean) => Callback)

  case class State(item: FunctionParameters, cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      // mark it as NOT cancelled (which is the default)
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
      // call parent handler with the new item and whether form was OK or cancelled
      props.submitHandler(state.item, state.cancelled)

    def updateDescription(e: ReactEventI) = {
      val text = e.target.value
      // update FunctionItem content
      t.modState(s => s.copy(item = s.item.copy(content = text)))
    }

    def updatePriority(e: ReactEventI) = {
      // update FunctionItem priority
      val newPri = e.currentTarget.value match {
        case p if p == FunctionHigh.toString => FunctionHigh
        case p if p == FunctionNormal.toString => FunctionNormal
        case p if p == FunctionLow.toString => FunctionLow
      }
      t.modState(s => s.copy(item = s.item.copy(priority = newPri)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a Function or two")
      val headerText = if (s.item.id == "") "Add new Function" else "Edit Function"
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        // footer has the OK button that submits the form before hiding it
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        // this is called after the modal has been hidden (animation is completed)
        closed = formClosed(s, p)),
        <.div(bss.formGroup,
          <.label(^.`for` := "description", "Description"),
          <.input.text(bss.formControl, ^.id := "description", ^.value := s.item.content,
            ^.placeholder := "write description", ^.onChange ==> updateDescription)),
        <.div(bss.formGroup,
          <.label(^.`for` := "priority", "Priority"),
          // using defaultValue = "Normal" instead of option/selected due to React
          <.select(bss.formControl, ^.id := "priority", ^.value := s.item.priority.toString, ^.onChange ==> updatePriority,
            <.option(^.value := FunctionHigh.toString, "High"),
            <.option(^.value := FunctionNormal.toString, "Normal"),
            <.option(^.value := FunctionLow.toString, "Low")
          )
        )
      )
    }
  }

  val component = ReactComponentB[Props]("FunctionForm")
    .initialState_P(p => State(p.item.getOrElse(FunctionParameters("", 0, "", FunctionNormal, completed = false))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}