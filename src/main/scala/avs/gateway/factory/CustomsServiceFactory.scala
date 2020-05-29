package avs.gateway.factory

import java.util.concurrent.LinkedBlockingQueue

import avs.concurrent.TruckQueue
import avs.gateway.document.{DocumentCheck, StepDocumentCheck}
import avs.gateway.generator.TrafficStepGenerator
import avs.gateway.truck.Truck
import avs.gateway.weight.{StepWeightCheck, WeightCheck}

object CustomsServiceFactory {

  def twoWeightCheckGatewaysFactoryMethod(inTraffic: LinkedBlockingQueue[Truck],
                                          outTraffic: LinkedBlockingQueue[Truck],
                                          size: Int):
  (DocumentCheck[Truck, TruckQueue], WeightCheck[Truck, TruckQueue], WeightCheck[Truck, TruckQueue], TruckQueue#Switch2) = {

    val firstQueue = new TruckQueue(size);
    val secondQueue = new TruckQueue(size);

    val documentGateway = new DocumentCheck[Truck, TruckQueue](inTraffic, firstQueue, secondQueue);
    val firstWeightGateway = new WeightCheck[Truck, TruckQueue](firstQueue, outTraffic);
    val secondWeightGateway = new WeightCheck[Truck, TruckQueue](secondQueue, outTraffic);

    val switch = new documentGateway.outTraffic1.Switch2(firstQueue, secondQueue)

    (documentGateway, firstWeightGateway, secondWeightGateway, switch)
  }


  def stepTwoWeightCheckGatewaysFactoryMethod(size: Int):
  (TrafficStepGenerator, StepDocumentCheck[Truck, TruckQueue], StepWeightCheck[Truck, TruckQueue],
    StepWeightCheck[Truck, TruckQueue], TruckQueue#Switch2) = {

    val inTraffic = new LinkedBlockingQueue[Truck]();
    val outTraffic = new LinkedBlockingQueue[Truck]();
    val generator = new TrafficStepGenerator(inTraffic)

    val firstQueue = new TruckQueue(size);
    val secondQueue = new TruckQueue(size);

    val documentGateway = new StepDocumentCheck[Truck, TruckQueue](inTraffic, firstQueue, secondQueue);
    val firstWeightGateway = new StepWeightCheck[Truck, TruckQueue](firstQueue, outTraffic);
    val secondWeightGateway = new StepWeightCheck[Truck, TruckQueue](secondQueue, outTraffic);

    val switch = new documentGateway.outTraffic1.Switch2(firstQueue, secondQueue)

    (generator, documentGateway, firstWeightGateway, secondWeightGateway, switch)
  }
}
