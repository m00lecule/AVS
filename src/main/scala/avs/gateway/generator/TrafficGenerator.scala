package avs.gateway.generator

import java.util.concurrent.LinkedBlockingQueue

import avs.gateway.truck.Truck

class TrafficGenerator(val outTraffic: LinkedBlockingQueue[Truck]) extends Runnable with TGeneratorLogger {
  val periodSeconds = 1

  override def run(): Unit = {
    while (true) {
      val t = Truck.random();
      outTraffic.put(t)
      loggerInfo(t)
      Thread.sleep(periodSeconds * 1000);
    }
  }
}


