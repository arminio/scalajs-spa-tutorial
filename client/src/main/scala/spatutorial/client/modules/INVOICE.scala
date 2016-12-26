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

object Invoice {

  case class Props(proxy: ModelProxy[Pot[Invoices]])

  case class State(selectedItem: Option[InvoiceItem] = None, showInvoiceForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the invoices, which will cause InvoiceStore to fetch invoices from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshInvoices))

    def editInvoice(item: Option[InvoiceItem]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showInvoiceForm = true))

    def invoiceEdited(item: InvoiceItem, cancelled: Boolean): CallbackTo[Unit] = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Invoice editing cancelled")
      } else {
        Callback.log(s"Invoice edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateInvoice(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showInvoiceForm = false))
    }

    def render(p: Props, s: State) =
      Panel(Panel.Props("What needs to be done"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 5000, _ => "Loading..."),
        p.proxy().render(invoices => InvoiceList(invoices.items, item => p.proxy.dispatchCB(UpdateInvoice(item)),
          item => editInvoice(Some(item)), item => p.proxy.dispatchCB(DeleteInvoice(item)))),
        Button(Button.Props(editInvoice(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showInvoiceForm) InvoiceForm(InvoiceForm.Props(s.selectedItem, invoiceEdited))
        else // otherwise add an empty placeholder
          Seq.empty[ReactElement])
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("INVOICE")
    .initialState(State()) // initial state from InvoiceStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Invoices]]) = component(Props(proxy))
}

object InvoiceForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[InvoiceItem], submitHandler: (InvoiceItem, Boolean) => Callback)

  case class State(item: InvoiceItem, cancelled: Boolean = true)

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
      // update InvoiceItem content
      t.modState(s => s.copy(item = s.item.copy(content = text)))
    }

    def updatePriority(e: ReactEventI) = {
      // update InvoiceItem priority
      val newPri = e.currentTarget.value match {
        case p if p == InvoiceHigh.toString => InvoiceHigh
        case p if p == InvoiceNormal.toString => InvoiceNormal
        case p if p == InvoiceLow.toString => InvoiceLow
      }
      t.modState(s => s.copy(item = s.item.copy(priority = newPri)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a invoice or two")
      val headerText = if (s.item.id == "") "Add new invoice" else "Edit invoice"
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
            <.option(^.value := InvoiceHigh.toString, "High"),
            <.option(^.value := InvoiceNormal.toString, "Normal"),
            <.option(^.value := InvoiceLow.toString, "Low")
          )
        )
      )
    }
  }

  val component = ReactComponentB[Props]("InvoiceForm")
    .initialState_P(p => State(p.item.getOrElse(InvoiceItem("", 0, "", InvoiceNormal, completed = false))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}