Sample Spring Boot project using the [Spring Cloud - Cloud Foundry Service Broker](https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker).

# Overview

This sample project uses the Spring Cloud - Cloud Foundry Service Broker to implement a MongoDB service. The MongoDB service also uses [spring-boot-data-mongodb](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters/spring-boot-starter-data-mongodb) to persist service instances and bindings.

## Getting Started

You need to install and run MongoDB somewhere and configure connectivity in [application.yml](src/main/resources/application.yml).

Build it:

    ./gradlew build

After building, you can push the broker app to Cloud Foundry or deploy it some other way and then [register it to Cloud Foundry](http://docs.cloudfoundry.org/services/managing-service-brokers.html#register-broker).



