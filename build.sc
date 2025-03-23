import mill._, scalalib._

object scalarx3 extends ScalaModule {
  def scalaVersion = "3.4.0"

  override def ivyDeps = Agg(
    ivy"com.lihaoyi::utest::0.7.10",
    ivy"com.lihaoyi::requests::0.6.9",
    ivy"com.lihaoyi::cask:0.8.0",
    ivy"dev.optics::monocle-core:3.3.0",
  )

  object test extends ScalaTests {
    override def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.8.2")
    def testFramework = "utest.runner.Framework"
  }
}