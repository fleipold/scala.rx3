import mill._, scalalib._

object app extends ScalaModule {
  def scalaVersion = "3.4.0"
  override def ivyDeps = Agg(
    ivy"com.lihaoyi::utest::0.7.10",
    ivy"com.lihaoyi::requests::0.6.9",
    ivy"com.lihaoyi::cask:0.8.0",
  )

}
