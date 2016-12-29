package spatutorial.client.services

import diode._
import diode.data._
import diode.react.ReactConnector
//import japgolly.scalajs.react.extra.router.Action
import spatutorial.shared._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions
case object LoadServices extends Action   //!@ should Load take a Loc so it can reload on any entry?
case class UpdateAllServices(services: Seq[Service]) extends Action
case class SaveService(service: Service) extends Action
case class LocTreeItemSelected(itemId : String) extends Action


case class Services(services: Seq[Service]) {
  def updated(newItem: Service): Services = {
    services.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Services(services:+ newItem)
      case idx =>
        // replace old
        Services(services.updated(idx, newItem))
    }
  }

  def remove(item: Service) = Services(services.filterNot(_ == item))
}



class TreeHandler[M](modelRW: ModelRW[M, Identifier]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {

    case LocTreeItemSelected(selectedItemId : String) =>
      println(s"handling tree node selection: $selectedItemId")
      updated(Identifier(value.str))

  }

}
class ServiceHandler[M](modelRW: ModelRW[M, Pot[Services]]) extends ActionHandler(modelRW) {

  val testServices = Seq(
    Service(id = Identifier("user1", "dev",  "SERVICE", "Suuid1"),
      serviceName = "service 1",
      provider = Provider("aws", "java8"),
      `package` = "target/scala-2.11/hello.jar",
      functions = Seq(
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid1"),"function 1", "handler 1", Nil)
      )
    ),
    Service(id = Identifier("user1", "dev",  "SERVICE", "Suuid2"),
      serviceName = "service 2",
      provider = Provider("aws", "java8"),
      `package` = "target/scala-2.11/hello.jar",
      functions = Seq(
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid0"),"armin function", "handler 1", Nil),
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid2"),"aydin function 2", "handler 1", Nil),
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid3"),"naz function 3", "handler 1", Nil),
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid4"),"Lara function 4", "handler 1", Nil),
        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid5"),"Lara function 5", "handler 1", Nil)
      )
    )
  )


  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case LoadServices =>
      println("load services")
      effectOnly(Effect(Future.successful(testServices).map(UpdateAllServices)))

    case UpdateAllServices(services: Seq[Service]) =>
      println(s"UpdateAllServicesL $services")
      updated(Ready(Services(services)))

    case SaveService(service) =>
      println(s"handling save service: $service")
      updated(value.map(_.updated(service)))

  }
}


// The base model of our application
case class RootModel(services: Pot[Services], selectedItemId: Identifier)


// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Identifier("NotSet","NotSet","NotSet"))
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(

    new ServiceHandler(zoomRW(_.services)((m, v) => m.copy(services = v))),
    new TreeHandler(zoomRW(_.selectedItemId)((m,v) => m.copy(selectedItemId = v)))
  )
}