package avs.gateway.truck

import java.util.concurrent.ThreadLocalRandom

import avs.gateway.document.{Document, TDocument}
import avs.gateway.weight.TWeight

case class Truck(override val doc: Document, override val weight: Int, override val id: Int) extends TDocument with TWeight with TID

object Truck {
  val maxWeight = 10000
  val minWeight = 4000
  val validProbability = 1
  var count = 0;

  def random(): Truck = {
    count += 1
    val weight: Int = ThreadLocalRandom.current.nextInt(Truck.minWeight, Truck.maxWeight + 1);
    val valid = math.random < validProbability;
    Truck(weight = weight, doc = Document(isValid = valid), id = count);
  }
}


