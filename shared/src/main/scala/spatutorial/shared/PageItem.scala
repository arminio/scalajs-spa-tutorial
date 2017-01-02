package spatutorial.shared

import java.util.UUID




case class Identifier(user: String , profile: String , kind: String , uuid: String = UUID.randomUUID().toString.replaceAll("-", "") ) {

  def str = {
    s"$user%$profile%$kind%$uuid".replaceAll("%", Identifier.separator) //!@ refactor this (list.mkstring("-"))
  }
}

case object Identifier {
  val empty = Identifier("no-user", "no-profile", "no-kind" , "no-uuid")

  val separator = "-"

  def apply(id: String): Identifier = {
    id.split(separator) match {
      case Array(u, p, k, uid) => Identifier(u, p, k, uid)
      case _ => throw new RuntimeException(s"Invalid identifier $id")
    }
  }
}

trait Kind[T]

case class Service(
                    id: Identifier,
                    serviceName: String,
                    provider: Provider,
                    `package`: String, // eg: path to jar
                    functions: Seq[Function] = Nil
                  ) //extends Kind[Service]


case class Provider(name: String = "aws", runtime: String = "java8")

case class Function(
                     id: Identifier,
                     name: String,
                     handler: String,
                     events: Seq[Event]
                   ) //extends Kind[Function]

//!@doco https://github.com/ochrons/boopickle#automatic-generation-of-hierarchy-picklers
sealed trait Event

case class HttpEvent(
                      method: String = "get",
                      path: String
                    ) extends Event


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
