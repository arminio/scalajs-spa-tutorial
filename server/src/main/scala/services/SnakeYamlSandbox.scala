package services

import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import org.yaml.snakeyaml.Yaml
import spatutorial.shared.{Function, Identifier}

/**
  * Created by armin.
  */
object SnakeYamlSandbox extends App {

  case class Color(name: String, red: Int, green: Int, blue: Int)
  case class Palette(name: String, colors: Option[List[Color]] = None)

  object PaletteYamlProtocol extends DefaultYamlProtocol {
    implicit val colorFormat = yamlFormat4(Color)
    implicit val paletteFormat = yamlFormat2(Palette)
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
  val palette = yaml.convertTo[Palette]

  println(palette.toYaml.prettyPrint)
  println("-" * 100)


}
