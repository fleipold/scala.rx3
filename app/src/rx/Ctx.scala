package rx


import scala.annotation.compileTimeOnly

object Ctx{

  object Data extends Generic[Data]{
    @compileTimeOnly("No implicit Ctx.Data is available here!")
    implicit object CompileTime extends Data(???)
  }
  class Data(rx0: => Rx.Dynamic[_]) extends Ctx(rx0)

  object Owner extends Generic[Owner]{
    @compileTimeOnly("No implicit Ctx.Owner is available here!")
    object CompileTime extends Owner(???)

    object Unsafe extends Owner(???){
      implicit val Unsafe: Ctx.Owner.Unsafe.type = this
    }

  }

  class Owner(rx0: => Rx.Dynamic[_]) extends Ctx(rx0)

  class Generic[T] {

  }
}

/**
  * An implicit scope representing a "currently evaluating" [[Rx]]. Used to keep
  * track of dependencies or ownership.
  */
class Ctx(rx0: => Rx.Dynamic[_]){
  lazy val contextualRx: Rx.Dynamic[_] = rx0
}

