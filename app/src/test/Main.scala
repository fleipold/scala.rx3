package test

object Main {

  def main(args: Array[String]): Unit = {
    println("Hello from src.")
    val x = rx.Var(10)
    val y: rx.Rx[Int] = x;

    println(x.now)
    println(y.now)
    x.update(11)

    println(y.now)
  }

}
