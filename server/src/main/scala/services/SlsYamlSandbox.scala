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

  println(new ApiService().services.toYaml.prettyPrint)
  println("-" * 100)
}
