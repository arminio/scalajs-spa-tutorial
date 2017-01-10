package services

import ammonite.ops._
import ammonite.ops.ImplicitWd._

import scala.util.Random

object AmmoniteSandBox {
  def main(args: Array[String]): Unit = {
//    val wd = /'tmp
    val dir = root / 'tmp / 'folder
    mkdir! dir
    %ls dir
//    println(pwd)
//    %cd dir
//    println(pwd)
    //    %('sls, 'create, "--help")
    val t1 = System.currentTimeMillis()
    %('serverless, 'create, "--template", "aws-java-gradle", "--path", s"myService-java-gradle${Random.nextInt()}")(dir)
    println((System.currentTimeMillis()-t1))
  }

}
