package avs.gateway.generator

import avs.gateway.Config
import avs.gateway.truck.Truck

trait TGeneratorLogger {
  def loggerInfo(t: Truck): Unit = {
    if (Config.DEBUG) {
      println(s"[GENERATOR] generated: ${t}")
    }
  }
}
