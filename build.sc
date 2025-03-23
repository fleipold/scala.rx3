import mill._, scalalib._

object scalarx3 extends ScalaModule {
  def scalaVersion = "3.6.4"

  override def ivyDeps = Agg(
    ivy"com.lihaoyi::utest::0.8.5",
    ivy"com.lihaoyi::requests::0.9.0",
    ivy"com.lihaoyi::cask:0.9.7",
    ivy"dev.optics::monocle-core:3.3.0",
  )

  object test extends ScalaTests {
    override def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.8.5")
    def testFramework = "utest.runner.Framework"
  }
}