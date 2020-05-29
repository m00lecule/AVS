package avs.gateway.weight

import avs.gateway.Config
import avs.gateway.truck.TID

trait TWeightCheckLogger[E <: TID with TWeight] {

  val id = WeightCheck.getID()

  def loggerInfoDepart(e: E): Unit ={
    if(Config.DEBUG){
      println(s"[WEIGHT-CHECK ${id}] id: ${e.id} with weight ${e.weight} has been let in")
    }
  }

  def loggerInfoArrival(e: E): Unit ={
    if(Config.DEBUG){
      println(s"[WEIGHT-CHECK ${id}] id: ${e.id} with weight ${e.weight} has arrived at WC")
    }
  }
}
