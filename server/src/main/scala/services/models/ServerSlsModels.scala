package services.models

import spatutorial.shared.HttpEvent

/**
  * Created by armin.
  */
class ServerSlsModels {

  case class ServerHttpEvent(event: HttpEvent) {

    def toYamlString: String = {
      "yml"
    }
  }
}
