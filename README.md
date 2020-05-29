# AVSystems Scala/Java Internship Task
Author: Michał Dygas
email: michaldygaz@protonmail.com

## Requirements
- Java Version 1.8.0_242
- Scala Version 2.13.2
- sbt 1.3.10

compatible with IDE:
- Intellij 2020.1

## Intro

This project is a projection of AVS internship exercise. 

System has been designed to asynchronously (all classes are also implemented with Runnable interface).

To prevent data races in asynchronous variant, I have decided to enhance ArrayBlockingQueue from java.utils to also support switching items between two queues.
The decision was dictated mostly by lack of required features, for example inserting item at concrete place to queue.  

## Packages

- ### java.avs.concurrent 
    this package contains enhanced versions of typical collections
    - **ArrayBlockingQueue** 
    - **SwitchableBlockingQueue** - implements switch feature between two queues, its is thread safe
    - **TruckQueue** - extends SwitchableBlockingQueue with tracking delay of all entries in queue
- ### scala.avs.gateway
    - **ConsoleApp** - Console application that directs GatewaySystem synchronously. For more details see **_ConsoleApp commands_** section. 
    - **AsyncApp** - Asynchronous system example.
    - **Config** 
        - **_DEBUG_** - to run application in debug mode (display logs from gateways)
        - **_stepMs_** - How long time step takes at StepWeightCheck 
    - **document**
        contains implementation of Document Gateway
        - **Document** - document representation
        - **DocumentCheck** - asynchronous document gateway
        - **StepDocumentCheck** - synchronous document gateway
        
    - **factory**
        - **CustomServiceFactory** - implement tho methods:
            - **_twoWeightCheckGatewaysFactoryMethod()_** - creates gateway systems' elements that are implementing Runnable interface
            - **_stepTwoWeightCheckGatewaysFactoryMethod()_** - creates synchronous gateway system
    - **generator** - Classes that purpose is to generate Trucks
        - **TrafficGenerator** - async generator
        - **TrafficStepGenerator** - synchronous generator
    - **provision**
        strategies of load balancing Trucks between TruckQueues that ends in WeighCheckGateway
        - **BlockingProvisionTraffic2** - strategy, that uses blocking operations on queues (if picked queue is full, it will wait for a free space)
        - **ProvisionTraffic2** - synchronous implementation of load balancing strategy. Picks the queue with the lowest sum of entries weight. If picked queue is full, then tries to insert Truck to other queue.   
    - **step** 
        - **TStep** - step interface
    - **system**
        - **CustomServiceSystem** - Controls step flow in synchronous  
    - **truck** - Representation of Truck in system
    - **weight**
        - **StepWeightCheck** - synchronous weight check gateway implementation
        - **WeightCheck** - weight check gateway that implements Runnable interface
       
     

## Run

### Terminal
```$xslt
    cd avs_internship/
    sbt run
```
Select app version:

```
Multiple main classes detected, select one to run:

 [1] avs.gateway.AsyncApp
 [2] avs.gateway.ConsoleApp
```

### Intellij
https://www.jetbrains.com/help/idea/running-applications.html#

## ConsoleApp commands


      switch <INDEX>         - switching entries between TruckQueues at specific index
      list   <ID>            - prints all entries assigned to queue with specific id
      delay  <ID> <INDEX>    - prints delay for entry specified by queue <ID> and position <INDEX>
      step                   - next step in the system
      step   <COUNT>         - next <COUNT> steps in the system
      step   --nogen         - step without generating random truck
      step   --nogen <COUNT> - next <COUNT> steps without generating random truck
      arrive <WEIGHT>        - generate Truck with specific weight