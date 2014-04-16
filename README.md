spring-boot-cf-service-broker-mongo
===========================

Sample spring boot project using the [spring-boot-starter-cf-service-broker](https://github.com/spgreenberg/spring-boot-starter-cf-service-broker)

# Overview

This sample project uses the [spring-boot-starter-cf-service-broker](https://github.com/spgreenberg/spring-boot-starter-cf-service-broker) to implement a MongoDB service.  The Mongo service also uses [spring-boot-starter-data-mongodb](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters/spring-boot-starter-data-mongodb) to persist service instances & bindings.

## Compatibility

* service broker API: 2.1
* cf-release: 152-163
* Pivotal CF: 1.1

## Getting Started

To deploy this project, be sure you first have followed the instructions for the [starter project here](https://github.com/spgreenberg/spring-boot-starter-cf-service-broker).  These instructions are temporary until the starter project is published to a maven repo.

You need to install MongoDB somewhere and configure connectivity in [MongoConfig.java](https://github.com/spgreenberg/spring-boot-cf-service-broker-mongo/blob/master/src/main/java/com/pivotal/cf/broker/mongodb/config/MongoConfig.java).

Build it:

	./gradlew build

That is it.  You can push the broker to CF or deploy it some other way and then register it as you normally would: [Services](http://docs.cloudfoundry.org/services/).



