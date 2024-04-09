package test


import rx.map
object Main {

  def main(args: Array[String]): Unit = {
    println("Hello from src.")
    val x = rx.Var(10)
    val y: rx.Rx[Int] = x;
    import rx.Ctx.Owner.Unsafe.Unsafe

    println(x.now)
    println(y.now)
    val z: rx.Rx[Int] = rx.Rx{
      x() + 10
    }
    val twice = y.map(x => x * 2)
    println(z.now)
    println(twice.now)
    x.update(11)

    println(y.now)
    println(z.now)
    println(twice.now)
  }

}
