// THIS IS AN AUTO-GENERATED FILE. DO NOT MODIFY.    
// generated by GenCounter on Fri Aug 01 15:06:06 PDT 2008
package scalanlp.counters;

import scala.collection.mutable.Map;
import scala.collection.mutable.HashMap;

/**
 * Count objects of type T with type Long.
 * This trait is a wraooer around Scala's Map trait
 * and can work with any scala Map. 
 *
 * @author dlwh
 */
trait LongCounter[T] extends Map[T,Long] {

  private var pTotal: Long = 0;

  /**
   * Return the sum of all values in the map.
   */
  def total() = pTotal;

  final protected def updateTotal(delta : Long) {
    pTotal += delta;
  }

  override def clear() {
    pTotal = 0;
    super.clear();
  }


  abstract override def update(k : T, v : Long) = {
    updateTotal(v - this(k))
    super.update(k,v);
  }

  // this isn't necessary, except that the jcl MapWrapper overrides put to call Java's put directly.
  override def put(k : T, v : Long) :Option[Long] = { val old = get(k); update(k,v); old}

  abstract override def -=(key : T) = {

    updateTotal(-this(key))

    super.-=(key);
  }

  /**
   * Increments the count by the given parameter.
   */
    def incrementCount(t : T, v : Long) = {
     update(t,(this(t) + v).asInstanceOf[Long]);
   }

  /**
   * Increments the count associated with T by Long.
   * Note that this is different from the default Map behavior.
  */
  override def +=(kv: (T,Long)) = incrementCount(kv._1,kv._2);

  override def default(k : T) : Long = 0;

  override def apply(k : T) : Long = super.apply(k);

  // TODO: clone doesn't seem to work. I think this is a JCL bug.
  override def clone(): LongCounter[T]  = super.clone().asInstanceOf[LongCounter[T]]

  /**
   * Return the T with the largest count
   */
   def argmax() : T = (elements reduceLeft ((p1:(T,Long),p2:(T,Long)) => if (p1._2 > p2._2) p1 else p2))._1

  /**
   * Return the T with the smallest count
   */
   def argmin() : T = (elements reduceLeft ((p1:(T,Long),p2:(T,Long)) => if (p1._2 < p2._2) p1 else p2))._1

  /**
   * Return the largest count
   */
   def max : Long = values reduceLeft ((p1:Long,p2:Long) => if (p1 > p2) p1 else p2)
  /**
   * Return the smallest count
   */
   def min : Long = values reduceLeft ((p1:Long,p2:Long) => if (p1 < p2) p1 else p2)

  // TODO: decide is this is the interface we want?
  /**
   * compares two objects by their counts
   */ 
   def comparator(a : T, b :T) = apply(a) compare apply(b);

  /**
   * Return a new DoubleCounter[T] with each Long divided by the total;
   */
   def normalized() : DoubleCounter[T] = {
    val normalized = new HashMap[T,Double]() with DoubleCounter[T];
    val total : Double = this.total
    for (pair <- elements) {
      normalized.put(pair._1,pair._2 / total)
    }
    normalized
  }

  /**
   * Return the sum of the squares of the values
   */
   def l2norm() : Double = {
    var norm = 0.0
    for (val v <- values) {
      norm += (v * v)
    }
    return Math.sqrt(norm)
  }

  /**
   * Return a List the top k elements, along with their counts
   */
   def topK(k : Int) = Counters.topK[(T,Long)](k,(x,y) => (x._2-y._2).asInstanceOf[Int])(this);

  /**
   * Return \sum_(t) C1(t) * C2(t). 
   */
  def dot(that : LongCounter[T]) : Double = {
    var total = 0.0
    for (val (k,v) <- that.elements) {
      total += get(k).asInstanceOf[Double] * v
    }
    return total
  }

  def +=(that : LongCounter[T]) {
    for(val (k,v) <- that.elements) {
      update(k,(this(k) + v).asInstanceOf[Long]);
    }
  }

}


object LongCounter {
  import it.unimi.dsi.fastutil.objects._
  import it.unimi.dsi.fastutil.ints._
  import it.unimi.dsi.fastutil.shorts._
  import it.unimi.dsi.fastutil.longs._
  import it.unimi.dsi.fastutil.floats._
  import it.unimi.dsi.fastutil.doubles._

  import scalanlp.counters.ints._
  import scalanlp.counters.shorts._
  import scalanlp.counters.longs._
  import scalanlp.counters.floats._
  import scalanlp.counters.doubles._


  import scala.collection.jcl.MapWrapper;
  class FastMapCounter[T] extends MapWrapper[T,Long] with LongCounter[T] {
    private val under = new Object2LongOpenHashMap[T];
    def underlying() = under.asInstanceOf[java.util.Map[T,Long]];
    override def apply(x : T) = under.getLong(x);
    override def update(x : T, v : Long) {
      val oldV = this(x);
      updateTotal(v-oldV);
      under.put(x,v);
    }
  }

  def apply[T]() = new FastMapCounter[T]();

  
  private def runtimeClass[T](x : Any) = x.asInstanceOf[AnyRef].getClass
  def apply[T](x : T) : LongCounter[T] = {
    val INT = runtimeClass(3);
    val LNG = runtimeClass(3l);
    val FLT = runtimeClass(3.0f);
    val SHR = runtimeClass(3.asInstanceOf[Short]);
    val DBL = runtimeClass(3.0);
    runtimeClass(x) match {
      case INT => Int2LongCounter().asInstanceOf[LongCounter[T]];
      case DBL => Double2LongCounter().asInstanceOf[LongCounter[T]];
      case FLT => Float2LongCounter().asInstanceOf[LongCounter[T]];
      case SHR => Short2LongCounter().asInstanceOf[LongCounter[T]];
      case LNG => Long2LongCounter().asInstanceOf[LongCounter[T]];
      case _ => LongCounter().asInstanceOf[LongCounter[T]];
    }
  }
      
}

