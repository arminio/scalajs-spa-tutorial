package spatutorial.shared

import org.scalatest.{FunSpec, Matchers}

class IdentifierSpec extends FunSpec with Matchers {

  describe("identifier") {
    it("should construct correctly using the default constructor") {
      val id = Identifier("FUNCTION", "user1", "profile1", "uuid123")

      id.str shouldBe("FUNCTION-user1-profile1-uuid123")
    }

    it("should construct correctly using the apply") {
      Identifier("FUNCTION", "user1", "profile1", "uuid123") shouldBe Identifier("FUNCTION-user1-profile1-uuid123")
    }
  }

}
