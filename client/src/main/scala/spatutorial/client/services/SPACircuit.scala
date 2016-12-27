package spatutorial.client.services

import autowire._
import diode._
import diode.data._
import diode.react.ReactConnector
import diode.util._
import spatutorial.shared._
import boopickle.Default._
import org.scalajs.dom
import spatutorial.shared.serverless.LambdaFunction

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

// Actions
case object LoadServices extends Action
case class UpdateAllServices(services: Seq[Service]) extends Action

//case object RefreshTodos extends Action
//
//case class UpdateAllTodos(todos: Seq[TodoItem]) extends Action
//
//case class UpdateTodo(item: TodoItem) extends Action
//
//case class DeleteTodo(item: TodoItem) extends Action
//
//// pageXX actions
//case object RefreshPageXXs extends Action
//
//case class UpdateAllPageXXs(pageXXs: Seq[PageXXItem]) extends Action
//
//case class UpdatePageXX(item: PageXXItem) extends Action
//
//case class DeletePageXX(item: PageXXItem) extends Action


////!@ Page actions
////!@ introduce PageIdendentifier case class in common project
//case class RefreshPage(pageIdentifier: PageIdentifier) extends Action // [pageName%???]
//case class UpdatePage(page: Page) extends Action





//case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
//  override def next(value: Pot[String]) = UpdateMotd(value)
//}



//case class Todos(items: Seq[TodoItem]) {
//  def updated(newItem: TodoItem) = {
//    items.indexWhere(_.id == newItem.id) match {
//      case -1 =>
//        // add new
//        Todos(items :+ newItem)
//      case idx =>
//        // replace old
//        Todos(items.updated(idx, newItem))
//    }
//  }
//  def remove(item: TodoItem) = Todos(items.filterNot(_ == item))
//}

case class Services(services: Seq[Service]) {

}



class ServiceHandler[M](modelRW: ModelRW[M, Pot[Services]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case LoadServices =>
      println("load services")
      val services = Seq(
        Service(id = "id1", artifactPath = "/tmp/xxx", functions = Seq(Function("fid1","function 1", "handler 1", Nil))),
        Service(id = "id2", artifactPath = "/tmp/xxx", functions = Seq(Function("fid2","function 2", "handler 2", Nil)))
      )
      effectOnly(Effect(Future.successful(services).map(UpdateAllServices)))

    case UpdateAllServices(services: Seq[Service]) =>
      println(s"UpdateAllServicesL $services")
      updated(Ready(Services(services)))
  }
}


///**
//  * Handles actions related to todos
//  *
//  * @param modelRW Reader/Writer to access the model
//  */
//class TodoHandler[M](modelRW: ModelRW[M, Pot[Todos]]) extends ActionHandler(modelRW) {
//  override def handle: PartialFunction[Any, ActionResult[M]] = {
//    case RefreshTodos =>
//      effectOnly(Effect(AjaxClient[Api].getAllTodos().call().map(UpdateAllTodos)))
//    case UpdateAllTodos(todos) =>
//      // got new todos, update model
//      updated(Ready(Todos(todos)))
//    case UpdateTodo(item) =>
//      // make a local update and inform server
//      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updateTodo(item).call().map(UpdateAllTodos)))
//    case DeleteTodo(item) =>
//      // make a local update and inform server
//      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deleteTodo(item.id).call().map(UpdateAllTodos)))
//  }
//}


// The base model of our application
case class RootModel(services: Pot[Services])


// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(

    new ServiceHandler(zoomRW(_.services)((m, v) => m.copy(services = v)))
  )
}