package avs.gateway.document

import avs.gateway.Config
import avs.gateway.truck.TID
import avs.gateway.weight.TWeight

trait TDocumentCheckLogger[E <: TWeight with TID] {
  def loggerInfo(e: E): Unit = {
    if (Config.DEBUG) {
      println(s"[DOCUMENT-CHECK] id: ${e.id} that weights ${e.weight} has arrived at DC")
    }
  }
}
