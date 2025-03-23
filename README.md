# scala.rx3

This is a sketch for porting Lihaoyi's Scala.Rx to Scala 3. The original implementation had to 
use macros to capture implicits and pass them into closures.

In Scala 3 the same result can be achieved by using [context functions](https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html#).

## Status Quo

A lot of the functionality of scala.rx is based on the apply function. Here is the original implementation: 

~~~scala
  /**
    * Constructs a new [[Rx]] from an expression (which explicitly takes an
    * [[Ctx.Owner]]) and an optional `owner` [[Ctx.Owner]].
    */
  def build[T](func: (Ctx.Owner, Ctx.Data) => T)(implicit owner: Ctx.Owner): Rx.Dynamic[T] = {
    require(owner != null, "owning RxCtx was null! Perhaps mark the caller lazy?")
    new Rx.Dynamic(func, if (owner == Ctx.Owner.Unsafe) None else Some(owner))
  }
~~~

Now, it would be quite tedious to always declare the owner and data context parameters whenever we want to
apply a function on some reactive variables.

The apply function has much nicer signature:


~~~scala
 /**
    * Constructs a new [[Rx]] from an expression, that will be re-run any time
    * an upstream [[Rx]] changes to re-calculate the value of this [[Rx]].
    *
    * Also injects an implicit [[Ctx.Owner]] into that block, which serves to keep
    * track of which other [[Rx]]s are used within that block (via their
    * `apply` methods) so this [[Rx]] can recalculate when upstream changes.
    */
  def apply[T](func: => T)(implicit ownerCtx: rx.Ctx.Owner): Rx.Dynamic[T] = macro Factories.rxApplyMacro[T]
~~~

Its implementation has to resort to using a macro which injects the contexts as
implicits into the captured argument (I think it is an excellent use of the macro facility, 
putting a bit of a burden on the  implementer of library, but being super intuitive to use),
somewhat like this:

~~~scala
    resetExpr[Rx.Dynamic[T]](c)(q"""_root_.rx.Rx.build{
      ($newOwnerCtx: _root_.rx.Ctx.Owner, $newDataCtx: _root_.rx.Ctx.Data) => $injected2
    }""")
~~~

## The Scala 3 - Way

Now in Scala 3 we can implement the apply method to take a context function from owner and data context and
wrap it into a function that takes owner and data context as explicit parameters, [like so](src/rx/Rx.scala:100):

~~~scala
  def apply[T](func: (rx.Ctx.Owner, rx.Ctx.Data) ?=> T)(implicit ownerCtx: rx.Ctx.Owner): Rx.Dynamic[T] =
   build(
    (ownerCtx, dataCtx) => {
      val r = func(using ownerCtx, dataCtx)
      r
  })(ownerCtx)
~~~

## State of This Repository

This is very much a PoC:

* The module set-up is overly simplistic, currently there is only a single module and no support for different target platforms.
* This repo was created by copying files that seemed relevant from the original repo and converting or commenting stuff
  out in order to make the BasicTests work.
* There are a couple of important operations that haven't been implemented yet (e.g.`filter` and `fold`)
* I have not fully grokked the concept of ownership, specifically the workings of the macros for `safe` and `unsafe` yet,
  so there are still some things to sort out.

### Todo:

* [ ] Figure out Ownership Context
* [ ] Implement missing operations
* [ ] Create proper multi-platform build

## Why scala.rx Matters

Scala.rx is a very pragmatic implementation of the concept of the reactive variable, which in turn is an amazing way
to implement UI models. The original [readme](https://github.com/lihaoyi/scala.rx?tab=readme-ov-file#related-work) 
mentions related work, such as `knockout.js`, which I used very effectively around 2012. However, some of the ideas 
goes back to the ValueModel pattern that is implemented in the VisualWorks Smalltalk UI library 
(they also had the concept of applying a lense to a reactive variable, they called it an `AspectAdapter`).

History aside, having a nice way to abstract variables that change over time means they can be composed into 
higher level abstractions that are still independent(ly testable) from the actual UI code.

An example would be `SelectionInList` which looks somewhat like this and can serve as a model for a drop down 
selection or some listbox:

```scala
class SelectionInList[T](
  val items: Var[Seq[T]] = Var(Seq.empty),
  val selectedItem: Var[Option[T]] = Var(None: Option[T])
){}
```

Composing the UI models / logic out of reactive variables improves reusability as well as testability. What is really
interesting is the fact that scala.rx supports jvm, js and native as target platforms (unlike airstream / laminar).
This enables unit testing from within the IDE using the excellent jvm test launcher and also the debugger even for 
UI models that are finally targeted at the browser (I still have to see an out-of the box javascript unit
test set-up that works). It also means they can be re-used with different UI-Frameworks, e.g. Browser, GTK, or JavaFX.

**Bottom line** after all that rambling: scala.rx provides a very nice set of primitives (including the `zoom` method!) 
to build complex user interfaces.