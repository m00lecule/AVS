package avs.gateway.provision

import java.util.concurrent.BlockingQueue

import avs.concurrent.IHasDelay
import avs.gateway.truck.TID

trait BlockingProvisionTraffic2[E <: TID, T <: BlockingQueue[E] with IHasDelay] extends TProvisionLogger[E] {
  val outTraffic1: T
  val outTraffic2: T

  def provision(e: E) {
    val delay1 = outTraffic1.getDelay;
    val delay2 = outTraffic2.getDelay;

    if (delay1 < delay2) {
      outTraffic1.put(e)
      loggerInfo(e, 1, outTraffic1.getDelay);
    } else {
      outTraffic2.put(e);
      loggerInfo(e, 2, outTraffic2.getDelay);
    }
  }
}