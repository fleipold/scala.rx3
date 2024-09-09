package rx

type Id[T] = T
extension[T] (node: Rx[T]) {

  def map[V](f: (rx.Ctx.Owner, rx.Ctx.Data) ?=> Id[T] => Id[V])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] = Rx.build { (ownerCtx, dataCtx) =>
    node.addDownstream(dataCtx)
    f(using ownerCtx, dataCtx)(node.toTry.get)
  }(ownerCtx)

  def flatMap[V](f:  (rx.Ctx.Owner, rx.Ctx.Data) ?=> Id[T] => Id[Rx[V]])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] =
    Rx.build { (ownerCtx, dataCtx) =>
      node.addDownstream(dataCtx)
      val inner = f(using ownerCtx, dataCtx)(node.toTry.get)
      inner.downStream.add(dataCtx.contextualRx)
      inner.now
    }(ownerCtx)


  def filter(f: Id[T] => Boolean)(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[T] = ???

  def fold[V](start: Id[V])(f: ((Id[V], Id[T]) => Id[V]))(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] = ???
/*
def foldImpl[V](start: Wrap[V],
                f: (rx.Ctx.Owner, rx.Ctx.Data) => (Wrap[V], Wrap[T]) => Wrap[V],
                enclosing: rx.Ctx.Owner): Rx.Dynamic[V] = {

  var prev: Wrap[V] = start
  Rx.build { (ownerCtx, dataCtx) =>
    prefix.addDownstream(dataCtx)
    prev = f(ownerCtx, dataCtx)(prev, this.get(prefix))
    this.unwrap(prev)
  }(enclosing)
}
 */


  def reduce(f: (Id[T], Id[T]) => Id[T])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[T] = ???

  def foreach(f: T => Unit)(implicit ownerCtx: Ctx.Owner): Obs = node.trigger(f(node.now))
}