package spatutorial.shared

import java.util.UUID


import scala.beans.BeanProperty


case class Identifier(kind: String, user: String, profile: String, uuid: String = UUID.randomUUID().toString.replaceAll("-", "")) {

  def str = {
    s"$kind%$user%$profile%$uuid".replaceAll("%", Identifier.separator) //!@ refactor this (list.mkstring("-"))
  }
}

case object Identifier {
  val empty = Identifier("nokind", "nouser", "noprofile", "nouuid")
  val services = Identifier("SERVICES", "a", "a", "a")

  val separator = "-"

  def apply(id: String): Identifier = {
    id.split(separator) match {
      case Array(k , u, p, uid) => Identifier(k, u, p, uid)
      case _ => throw new RuntimeException(s"Invalid identifier $id")
    }
  }
}


case class Service(id: Identifier,
                   serviceName: String,
                    provider: Provider,
                    `package`: String, // eg: path to jar
                    functions: Map[String, Function] = Map.empty)


case class Provider(name: String = "aws", runtime: String = "java8")


case class Function(@BeanProperty id: Identifier,
                    @BeanProperty name: String,
                    @BeanProperty handler: String,
                    @BeanProperty httpEvents: Seq[HttpEvent])

//!@doco https://github.com/ochrons/boopickle#automatic-generation-of-hierarchy-picklers
sealed trait Event {
  //  def toYamlString: String
}

case class HttpEvent(method: String = "get",
                     path: String)

//!@
//import java.io.File
//import java.util.UUID
//
//
//case class Service(id: String, artifactPath: String, functions: Seq[Function]) {
////  def apply(artifactPath: String, fs: Seq[Function]): Service = Service(UUID.randomUUID().toString, artifactPath, fs)
//}
//
//case class Function(id:String, name: String, handler: String, events: Seq[Event])
//
//trait Event
//case class HttpEvent(path: String,
//                     method: String, //!@ Enum this
//                     cors: Boolean) extends Event
//
////!@ Enum this
//trait PageItemType
//
//trait PageItemTypeAndData {
//  def getType: PageItemType
//
//  def getData(): (PageIdentifier) => Seq[String]
//
//}
//
//case class PageItem(name: String,
//                    label: String,
//                    typeAndData: PageItemTypeAndData) {
//
//
//  //!@
//
//}
//
//class Page(val title: String, val items: Seq[PageItem], val saveFunction: SaveFunction)
//
