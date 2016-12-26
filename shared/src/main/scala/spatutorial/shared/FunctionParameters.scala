package spatutorial.shared

import boopickle.Default._

sealed trait FunctionPriority

case object FunctionLow extends FunctionPriority

case object FunctionNormal extends FunctionPriority

case object FunctionHigh extends FunctionPriority




trait TypeAndData

//!@1 rename to Parameters
case class FunctionParameters(id: String,
                              name: String,
                              description: String,
                              label: String,
                              typeAndData: TypeAndData)

object FunctionPriority {
  implicit val FunctionPriorityPickler: Pickler[FunctionPriority] = generatePickler[FunctionPriority]
}
