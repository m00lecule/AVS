package avs.gateway.document

import java.util.concurrent.LinkedBlockingQueue

import avs.concurrent.{ArrayBlockingQueue, IHasDelay}
import avs.gateway.provision.ProvisionTraffic2
import avs.gateway.step.TStep
import avs.gateway.truck.TID
import avs.gateway.weight.TWeight

class StepDocumentCheck[E <: TDocument with TID with TWeight, T <: ArrayBlockingQueue[E] with IHasDelay](val inTraffic: LinkedBlockingQueue[E],
                                                                                                         override val outTraffic1: T,
                                                                                                         override val outTraffic2: T)
  extends ProvisionTraffic2[E, T] with TStep with TDocumentCheckLogger[E] {

  def documentCheckIn(e: E): Boolean = {
    e.doc.isValid
  }

  var current: Option[E] = None

  override def step(): Unit = {
    var isProcessing = true

    while (isProcessing) {
      if (current.isDefined) {
        isProcessing = processCurrent()
      } else {
        isProcessing = readNextAndCheck()
      }
    }
  }

  def readNextAndCheck(): Boolean = {
    val entry = inTraffic.poll();
    var ret: Boolean = false;

    if (entry != null) {
      loggerInfo(entry)

      if (documentCheckIn(entry)) {
        current = Some(entry)
      }
      ret = true;
    } else {
      current = None
      ret = false
    }
    ret
  }

  def processCurrent(): Boolean = {
    if (offerProvision(current.get)) {
      current = None
      return true
    }
    false
  }

  def getTruckDelay(id: Int): Option[Int] = {
    var delay: Int = outTraffic1.getDelayForId(id)
    if (!delay.equals(-1)) {
      return Some(delay)
    }
    delay = outTraffic2.getDelayForId(id)
    if (!delay.equals(-1)) {
      return Some(delay)
    }
    None
  }
}
