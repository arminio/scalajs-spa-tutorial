package spatutorial.client.services

import autowire._
import diode._
import diode.data._
import diode.react.ReactConnector
import diode.util._
import spatutorial.shared.{Api, InvoiceItem, TodoItem}
import boopickle.Default._
import org.scalajs.dom

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

// Actions
case object RefreshTodos extends Action

case class UpdateAllTodos(todos: Seq[TodoItem]) extends Action

case class UpdateTodo(item: TodoItem) extends Action

case class DeleteTodo(item: TodoItem) extends Action

// invoice actions
case object RefreshInvoices extends Action

case class UpdateAllInvoices(invoices: Seq[InvoiceItem]) extends Action

case class UpdateInvoice(item: InvoiceItem) extends Action

case class DeleteInvoice(item: InvoiceItem) extends Action



case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

// The base model of our application
case class RootModel(todos: Pot[Todos], motd: Pot[String], invoices: Pot[Invoices])

case class Todos(items: Seq[TodoItem]) {
  def updated(newItem: TodoItem) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Todos(items :+ newItem)
      case idx =>
        // replace old
        Todos(items.updated(idx, newItem))
    }
  }
  def remove(item: TodoItem) = Todos(items.filterNot(_ == item))
}

case class Invoices(items: Seq[InvoiceItem]) {
  def updated(newItem: InvoiceItem) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Invoices(items :+ newItem)
      case idx =>
        // replace old
        Invoices(items.updated(idx, newItem))
    }
  }
  def remove(item: InvoiceItem) = Invoices(items.filterNot(_ == item))
}

/**
  * Handles actions related to todos
  *
  * @param modelRW Reader/Writer to access the model
  */
class TodoHandler[M](modelRW: ModelRW[M, Pot[Todos]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshTodos =>
      effectOnly(Effect(AjaxClient[Api].getAllTodos().call().map(UpdateAllTodos)))
    case UpdateAllTodos(todos) =>
      // got new todos, update model
      updated(Ready(Todos(todos)))
    case UpdateTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updateTodo(item).call().map(UpdateAllTodos)))
    case DeleteTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deleteTodo(item.id).call().map(UpdateAllTodos)))
  }
}
/**
  * Handles actions related to invoices
  *
  * @param modelRW Reader/Writer to access the model
  */
class InvoiceHandler[M](modelRW: ModelRW[M, Pot[Invoices]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshInvoices =>
//      dom.window.debugger();
      println("RefreshInvoices")
      effectOnly(Effect(AjaxClient[Api].getAllInvoices().call().map(UpdateAllInvoices)))
    case UpdateAllInvoices(invoices) =>
      // got new invoices, update model
      println(s"UpdateAllInvoices $invoices")
       js.debugger()
      updated(Ready(Invoices(invoices)))
    case UpdateInvoice(item) =>
      // make a local update and inform server
      println(s"UpdateInvoice $item")

      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updateInvoice(item).call().map(UpdateAllInvoices)))
    case DeleteInvoice(item) =>
      // make a local update and inform server
      println(s"DeleteInvoice $item")

      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deleteInvoice(item.id).call().map(UpdateAllInvoices)))
  }
}

/**
  * Handles actions related to the Motd
  *
  * @param modelRW Reader/Writer to access the model
  */
class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[Api].welcomeMsg("User X").call())(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new TodoHandler(zoomRW(_.todos)((m, v) => m.copy(todos = v))),
    new InvoiceHandler(zoomRW(_.invoices)((m, v) => m.copy(invoices = v))),
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
  )
}