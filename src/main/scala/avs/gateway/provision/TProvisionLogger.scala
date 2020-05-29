package avs.gateway.provision

import avs.gateway.Config
import avs.gateway.truck.TID

trait TProvisionLogger[E <: TID] {
  def loggerInfo(e: E, qid: Int, delay: Int): Unit = {
    if (Config.DEBUG) {
      println(s"[DOCUMENT-CHECK] id: ${e.id} has been accepted and provisioned to queue ${qid}, current delay: ${delay}")
    }
  }
}
