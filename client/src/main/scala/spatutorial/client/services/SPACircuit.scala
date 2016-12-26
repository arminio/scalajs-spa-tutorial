package spatutorial.client.services

import autowire._
import diode._
import diode.data._
import diode.react.ReactConnector
import diode.util._
import spatutorial.shared.{Api, PageXXItem, TodoItem}
import boopickle.Default._
import org.scalajs.dom

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

// Actions
case object RefreshTodos extends Action

case class UpdateAllTodos(todos: Seq[TodoItem]) extends Action

case class UpdateTodo(item: TodoItem) extends Action

case class DeleteTodo(item: TodoItem) extends Action

// pageXX actions
case object RefreshPageXXs extends Action

case class UpdateAllPageXXs(pageXXs: Seq[PageXXItem]) extends Action

case class UpdatePageXX(item: PageXXItem) extends Action

case class DeletePageXX(item: PageXXItem) extends Action



case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

// The base model of our application
case class RootModel(todos: Pot[Todos], motd: Pot[String], pageXXs: Pot[PageXXs])

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

case class PageXXs(items: Seq[PageXXItem]) {
  def updated(newItem: PageXXItem) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        PageXXs(items :+ newItem)
      case idx =>
        // replace old
        PageXXs(items.updated(idx, newItem))
    }
  }
  def remove(item: PageXXItem) = PageXXs(items.filterNot(_ == item))
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
  * Handles actions related to pageXXs
  *
  * @param modelRW Reader/Writer to access the model
  */
class PageXXHandler[M](modelRW: ModelRW[M, Pot[PageXXs]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshPageXXs =>
//      dom.window.debugger();
      println("RefreshPageXXs")
      effectOnly(Effect(AjaxClient[Api].getAllPageXXs().call().map(UpdateAllPageXXs)))
    case UpdateAllPageXXs(pageXXs) =>
      // got new pageXXs, update model
      println(s"UpdateAllPageXXs $pageXXs")
       js.debugger()
      updated(Ready(PageXXs(pageXXs)))
    case UpdatePageXX(item) =>
      // make a local update and inform server
      println(s"UpdatePageXX $item")

      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updatePageXX(item).call().map(UpdateAllPageXXs)))
    case DeletePageXX(item) =>
      // make a local update and inform server
      println(s"DeletePageXX $item")

      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deletePageXX(item.id).call().map(UpdateAllPageXXs)))
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
    new PageXXHandler(zoomRW(_.pageXXs)((m, v) => m.copy(pageXXs = v))),
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
  )
}