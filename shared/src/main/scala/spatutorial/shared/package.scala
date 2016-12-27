package spatutorial

package object shared {
  type PageXXNumber = Option[String]
  type ReferenceNumber = Option[String]
  type Customer = Option[String]
  type Amount = BigDecimal
  type Status = String

  type PageIdentifier = String
  type SaveFunction = Unit => Unit


}


