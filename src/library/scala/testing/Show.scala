package scala.testing

/** Classes inheriting trait `Show` can test their member methods using the notattion
 *  'meth(arg_1, ..., arg_n), where `meth' is the name of the method and `arg_1,...,arg_n' are
 *  the arguments. The only difference to a normal method call is the leading quote character (').
 *  A quoted method call like the one above will produces a legible diagnostic to be printed on Console.
 *  It is of the form
 *
 *    meth(arg_1, ..., arg_n)  gives  <result>
 *
 *  where <result> is the result of evaluating the call.
 */
trait Show {

  /** The result class of wrapper `symApply`.
   *  Prints out diagnostics of method applications.
   */
  class SymApply(f: Symbol) {
    def apply[A](args: A*) {
      println(test(f, args: _*))
    }
  }

  /** An implicit definition that adds an apply method to Symbol which forwards to `test`.
   */
  implicit def symApply(sym: Symbol) = new SymApply(sym)

  /** Apply method with name of given symbol `f` to given arguments and return
   *  a result diagnostics.
   */
  def test[A](f: Symbol, args: A*): String = {
    val args1 = args map (_.asInstanceOf[AnyRef])
    getClass.getMethods.toList filter (_.getName == f.name) match {
      case List(meth) =>
        f.name+"("+(args mkString ",")+")  gives  "+
        {
          try {
            meth.invoke(this, args1: _*)
          } catch {
            case ex: IllegalAccessException => ex
            case ex: IllegalArgumentException => ex
            case ex: java.lang.reflect.InvocationTargetException => ex
          }
        }
      case List() =>
        f.name+" is not defined"
      case _ =>
        "cannot disambiguate between multiple implementations of "+f.name
    }
  }
}
