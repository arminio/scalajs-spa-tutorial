package services

import monocle.function.FilterIndexFunctions
import monocle.syntax.ApplySyntax
import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import spatutorial.shared._

/**
  * Created by armin.
  */
object SlsYamlSandbox extends App with ApplySyntax with FilterIndexFunctions {

  object PaletteYamlProtocol extends DefaultYamlProtocol {
    implicit val identifierFormat = yamlFormat4((user: String, profile: String, kind: String, uuid: String) => Identifier.apply(kind, user, profile, uuid))
//    implicit val eventFormat = yamlFormat0(spatutorial.shared.Event)
    implicit val httpEventFormat = yamlFormat2(HttpEvent)
    implicit val functionFormat = yamlFormat4(Function)
    implicit val providerFormat = yamlFormat2(Provider)
    implicit val serviceFormat = yamlFormat5(Service)
  }

  import PaletteYamlProtocol._
  import net.jcazevedo.moultingyaml._

  val yaml = """name: My Palette
               |colors:
               |- name: color 1
               |  red: 1
               |  green: 1
               |  blue: 1
               |- name: color 2
               |  red: 2
               |  green: 2
               |  blue: 2
               |""".stripMargin.parseYaml
//  val palette = yaml.convertTo[Palette]

//  case class Foo(name:String, prop1: String, prop2: Int)
//  object MyYamlProtocol extends DefaultYamlProtocol {
//    implicit object ColorYamlFormat extends YamlFormat[Foo] {
//      def write(c: Foo) =
//        YamlArray(
//          YamlString(c.name),
//          YamlObject(
//            (YamlString("prop1") -> YamlString(c.prop1)),
//            (YamlString("prop2") -> YamlNumber(c.prop2)))
//        )
//
//      def read(value: YamlValue) = value match {
//        case YamlArray(
//        Vector(
//        YamlString(name),
//        YamlString(p1: String),
//        YamlNumber(p2))) =>
//          new Foo(name, p1, p2.intValue())
//        case _ => deserializationError("Foo expected")
//      }
//    }
//  }


//  import monocle.Lens
//  import monocle.macros.GenLens
////  import monocle.syntax.ApplyTraversalOps._
//
//  val functions: Lens[Service, Iterable[Function]] = GenLens[Service](_.functions.values)
//
//  val funcHandler = GenLens[Function](_.handler)
//  val funcId = GenLens[Function](_.id)
////  val address   : Lens[Ser , Address] = GenLens[Company](_.address)
//  val street    : Lens[Address , Street]  = GenLens[Address](_.street)
//  val streetName: Lens[Street  , String]  = GenLens[Street](_.name)

  case class Street(name: String)
  case class Address(street: Option[Street])
  case class Person(addresses: List[Address])


  val person = Person(List(
    Address(Some(Street("1 Functional Rd."))),
    Address(Some(Street("2 Imperative Dr.")))
  ))



//  val newServices = services.map(_.functions.map{case (n, fn) => (n -> fn.copy(handler = "${variable-${demo-stage}}"))})
////  println(services.toYaml.prettyPrint)
//  println(newServices.toYaml.prettyPrint)
//  import MyYamlProtocol._

//  val yaml2 = new Foo("CadetBlue", "95", 158).toYaml
////  val color = yaml2.convertTo[Foo]
//  println(yaml2.prettyPrint)
  println("-" * 100)
}
