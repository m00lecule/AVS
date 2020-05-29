package avs.gateway.system

import avs.gateway.factory.CustomsServiceFactory

class CustomServiceSystem {

  val (generator, documentGateway, weightGateway1, weightGateway2, switch) = CustomsServiceFactory.stepTwoWeightCheckGatewaysFactoryMethod(5);

  def step() = {
    stepNoGen()
    generator.step()
  }

  def stepNoGen() = {
    weightGateway1.step()
    weightGateway2.step()
    documentGateway.step()
  }
}
