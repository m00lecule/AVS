package avs.gateway.document

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import avs.concurrent.IHasDelay
import avs.gateway.provision.BlockingProvisionTraffic2
import avs.gateway.truck.TID
import avs.gateway.weight.TWeight


class DocumentCheck[E <: TDocument with TID with TWeight, T <: BlockingQueue[E] with IHasDelay](val inTraffic: LinkedBlockingQueue[E],
                                                                                                override val outTraffic1: T,
                                                                                                override val outTraffic2: T)
  extends Runnable with BlockingProvisionTraffic2[E, T] with TDocumentCheckLogger[E] {

  override def run(): Unit = {
    while (true) {
      val entry = inTraffic.take();
      loggerInfo(entry)

      if (documentCheckIn(entry)) {
        provision(entry)
      }
    }
  }

  def documentCheckIn(e: E): Boolean = {
    e.doc.isValid
  }
}

object DocumentCheck {
  val sleepTime = 500;
}
