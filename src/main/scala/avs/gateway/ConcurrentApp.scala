package avs.gateway

import java.util.concurrent.{ExecutorService, Executors, LinkedBlockingQueue, TimeUnit}

import avs.gateway.factory.CustomsServiceFactory
import avs.gateway.generator.TrafficGenerator
import avs.gateway.truck.Truck

object ConcurrentApp extends App {

  Config.DEBUG = true
  val queueSize = 5
  val poolSize = 6
  val inTraffic = new LinkedBlockingQueue[Truck]();
  val outTraffic = new LinkedBlockingQueue[Truck]();
  val generator = new TrafficGenerator(inTraffic)
  val (documentGateway, weightGateway1, weightGateway2, switch) = CustomsServiceFactory
    .twoWeightCheckGatewaysFactoryMethod(inTraffic, outTraffic, queueSize)
  val tasks: List[Runnable] = List(generator, documentGateway, weightGateway1, weightGateway2)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  tasks.map(t => pool.execute(t))
  pool.shutdown()

  pool.awaitTermination(Long.MaxValue, TimeUnit.MINUTES);
}
