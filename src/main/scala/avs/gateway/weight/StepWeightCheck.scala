package avs.gateway.weight

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import avs.concurrent.IHasDelay
import avs.gateway.step.TStep
import avs.gateway.Config
import avs.gateway.truck.TID

class StepWeightCheck[E <: TWeight with TID, T <: BlockingQueue[E] with IHasDelay](val inTraffic: T,
                                                                                   val outTraffic: LinkedBlockingQueue[E])

  extends TStep with TWeightCheckLogger[E] {

  var current: Option[E] = None;
  var waitTime = 0;

  override def step(): Unit = {
    if (current.isDefined) {
      processCurrent()
    } else {
      readNext()
    }
  }

  def processCurrent() = {
    waitTime -= Config.stepMs
    inTraffic.decrementDelay(Config.stepMs);

    if (waitTime <= 0 && current.isDefined) {
      loggerInfoDepart(current.get)
      outTraffic.put(current.get)
      current = None
      waitTime = 0;
    } else {
      logRemainingTime()
    }
  }

  def readNext() = {
    val t = inTraffic.poll()
    if (t != null) {
      loggerInfoArrival(t)
      current = Some(t)
      waitTime = t.weight
      processCurrent()
    } else {
      current = None
    }
  }

  def logRemainingTime() = {
    if (Config.DEBUG && current.isDefined) {
      println(s"[WEIGHT-CHECK] id: ${current.get.id} with remaining ${waitTime} timeunits in next step")
    }
  }
}