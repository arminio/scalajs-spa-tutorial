package spatutorial.shared

import org.scalatest.{FunSpec, Matchers}

class IdentifierSpec extends FunSpec with Matchers {

  describe("identifier") {
    it("should construct correctly using the default constructor") {
      val id = Identifier("user1", "profile1", "FUNCTION", "uuid123")

      id.str shouldBe("user1%profile1%FUNCTION%uuid123")
    }

    it("should construct correctly using the apply") {
      Identifier("user1", "profile1", "FUNCTION", "uuid123") shouldBe Identifier("user1%profile1%FUNCTION%uuid123")
    }
  }

}
