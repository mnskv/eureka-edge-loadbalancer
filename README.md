first u need to up the eureka server up, then cloud server up-> parent service-> atomic service
below are components
 
Java Version 17
Spring boot 3.x
Service Discovery Server
Edge Server / API Gateway
Dynamic Routing and Load Balancer
Config Server
Composite Services (Controller)
Core Services(Atomic Services
Circuit Breaker













serviceConsumer--> loadbalancer(edgeserver)-->product-composite(service-discovery)-->atomicservices(review, recommendation,product)




