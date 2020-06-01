package avs.gateway

import avs.concurrent.TruckQueue
import avs.gateway.system.CustomServiceSystem

object ConsoleApp extends App {

  Config.DEBUG = true
  val queueSize = 5

  val system = new CustomServiceSystem

  console()

  def console(): Unit = {
    while (true) {
      print(">> ")
      val line = Console.in.readLine().trim.replaceAll(" +", " ")
      action(line)
    }
  }

  def action(line: String): Unit = {
    line match {
      case "step" => step()
      case "step --nogen" => stepNoGen()
      case s"step $count" if isNumber(count) => for (_ <- 0 to count.toInt) step()
      case s"step --nogen $count" if isNumber(count) => for (_ <- 0 to count.toInt) stepNoGen()
      case s"list $id" if isNumber(id) => listItems(id.toInt)
      case s"switch $index" if isNumber(index) => switch(index.toInt)
      case s"delay $id" if isNumber(id) => getDelayId(id.toInt)
      case s"delay $id $index" if isNumber(id) && isNumber(index) => delayAt(id.toInt, index.toInt)
      case s"arrive $weight" if isNumber(weight) => arrive(weight.toInt)
      case _ => loggerInfo()
    }
  }

  def isNumber(str: String) = str.forall(_.isDigit)

  def step() = {
    system.step()
  }

  def stepNoGen() = {
    system.stepNoGen()
  }

  def listItems(id: Int): Unit = {
    var trafficQueue: TruckQueue = null;

    if (id == 1) {
      trafficQueue = system.documentGateway.outTraffic1
      trafficQueue.print()
    } else if (id == 2) {
      trafficQueue = system.documentGateway.outTraffic2
      trafficQueue.print()
    } else {
      println("[ERROR] Queue ID must be equal 1 or 2")
    }
  }

  def switch(index: Int): Unit = {
    system.switch.replaceAt(index)
  }

  def delayAt(queueId: Int, index: Int): Unit = {
    var trafficQueue: TruckQueue = null;

    if (queueId == 1) {
      trafficQueue = system.documentGateway.outTraffic1;
      println(trafficQueue.delayAt(index))
    } else if (queueId == 2) {
      trafficQueue = system.documentGateway.outTraffic2;
      println(trafficQueue.delayAt(index))
    } else {
      println("[ERROR] Queue ID must be equal 1 or 2")
    }
  }

  def getDelayId(id: Int) = {
    system.documentGateway.getTruckDelay(id) match {
      case Some(delay) =>
        println(s"$delay")
      case None =>
        println("Truck has not been found")
    }
  }

  def arrive(weight: Int): Unit = {
    system.generator.arrive(weight);
  }

  def loggerInfo() = {
    println("\n===========================================FEATURES===========================================")
    println("switch <INDEX>         - switching entries between TruckQueues at specific index")
    println("list   <ID>            - prints all entries assigned to queue with specific ID")
    println("delay  <ID>            - prints delay for truck that ID is <ID>")
    println("delay  <ID> <INDEX>    - prints delay for entry specified by queue ID and position index")
    println("step                   - next step in the system")
    println("step   <COUNT>         - next <COUNT> steps in the system")
    println("step   --nogen         - step without generating random truck")
    println("step   --nogen <COUNT> - next <COUNT> steps without generating random truck")
    println("arrive <WEIGHT>        - generate Truck with specific weight\n\n")
  }
}
