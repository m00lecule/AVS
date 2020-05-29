package avs.gateway.provision


import avs.concurrent.{ArrayBlockingQueue, IHasDelay}
import avs.gateway.truck.TID

trait ProvisionTraffic2[E <: TID, T <: ArrayBlockingQueue[E] with IHasDelay] extends TProvisionLogger[E] {
  val outTraffic1: T
  val outTraffic2: T

  def offerProvision(e: E): Boolean = {
    val delay1 = outTraffic1.getDelay;
    val delay2 = outTraffic2.getDelay;
    var ret: Boolean = false;

    if (outTraffic2.isFull() || delay1 < delay2) {
      ret = offerItem(e, outTraffic1, 1)
    } else {
      ret = offerItem(e, outTraffic2, 2);
    }
    ret
  }

  def offerItem(e: E, outTraffic: T, qid: Int): Boolean = {
    if (outTraffic.isFull)
      return false;

    val isProvisioned = outTraffic.offer(e)

    if (isProvisioned)
      loggerInfo(e, qid, outTraffic.getDelay);

    isProvisioned
  }
}