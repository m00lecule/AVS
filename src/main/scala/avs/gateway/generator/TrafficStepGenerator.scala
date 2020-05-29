package avs.gateway.generator

import java.util.concurrent.LinkedBlockingQueue

import avs.gateway.step.TStep
import avs.gateway.truck.Truck

class TrafficStepGenerator(val outTraffic: LinkedBlockingQueue[Truck]) extends TStep with TGeneratorLogger {
  override def step(): Unit = {
    val t = Truck.random();
    outTraffic.put(t)
    loggerInfo(t)
  }

  def arrive(weight: Int) {
    var t = Truck.random();
    t = Truck(doc = t.doc, weight = weight, id = t.id)
    outTraffic.put(t)
    loggerInfo(t)
  }
}