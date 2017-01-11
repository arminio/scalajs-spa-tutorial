package services

import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import spatutorial.shared._

/**
  * Created by armin.
  */
object SlsYamlSandbox extends App {

  object PaletteYamlProtocol extends DefaultYamlProtocol {
    implicit val identifierFormat = yamlFormat4(Identifier.apply)
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


  private val services = new ApiService().services
//  for {
//    s <- services
//    (name,fn) <- s.functions
//  } yield

  val newServices = services.map(_.functions.map{case (n, fn) => (n -> fn.copy(handler = "${variable-${demo-stage}}"))})
//  println(services.toYaml.prettyPrint)
  println(newServices.toYaml.prettyPrint)
//  import MyYamlProtocol._

//  val yaml2 = new Foo("CadetBlue", "95", 158).toYaml
////  val color = yaml2.convertTo[Foo]
//  println(yaml2.prettyPrint)
  println("-" * 100)
}
