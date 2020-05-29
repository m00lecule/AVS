package avs.gateway.weight

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import avs.concurrent.IHasDelay
import avs.gateway.truck.TID

class WeightCheck[E <: TWeight with TID, T <: BlockingQueue[E] with IHasDelay](val inTraffic: T,
                                                                               val outTraffic: LinkedBlockingQueue[E])

  extends Runnable with TWeightCheckLogger[E] {
  override def run(): Unit = {
    while (true) {
      val entry = inTraffic.take();
      loggerInfoArrival(entry)
      Thread.sleep(entry.weight)
      outTraffic.add(entry)
      loggerInfoDepart(entry)
    }
  }
}

object WeightCheck {
  private var count: Int = 0;

  def getID(): Int = {
    count += 1
    count
  }
}