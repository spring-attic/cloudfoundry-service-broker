Sample Spring Boot project using the [Spring Cloud - Cloud Foundry Service Broker 1.x](https://github.com/spring-cloud/spring-cloud-cloudfoundry-service-broker/tree/1.0.x).

**Note:** This sample will not be updated to be compatible with `Spring Cloud Open Service Broker` 2.x. A separate Spring Cloud Open Service Broker 2.x [sample project](https://github.com/spring-cloud-samples/bookstore-service-broker) is available. 

# Overview

This sample project uses the Spring Cloud - Cloud Foundry Service Broker to implement a MongoDB service. The MongoDB service also uses [spring-boot-data-mongodb](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters/spring-boot-starter-data-mongodb) to persist service instances and bindings.

## Getting Started

You need to install and run MongoDB somewhere and configure connectivity in [application.yml](src/main/resources/application.yml).

Build it:

    ./gradlew build

After building, you can push the broker app to Cloud Foundry or deploy it some other way and then [register it to Cloud Foundry](http://docs.cloudfoundry.org/services/managing-service-brokers.html#register-broker).


## Enable Auth in your MongoDB instance

Add the Initial Admin User:
```
$ mongo
> use admin
> db.createUser({ user: 'admin', pwd: 'password', roles: [{"role" : "readWriteAnyDatabase","db" : "admin"},{"role" : "userAdminAnyDatabase","db" : "admin"}] });
```

Update your mongod.conf file to enable authorization. For example, add lines like these: 

```
security:
  authorization: enabled
```

Restart your Mongo service and test that authentication is working as expected: 

`mongo --authenticationDatabase "admin" -u "admin" -p "password"`

Refer to the MongoDB docs for more details: https://docs.mongodb.com/manual/tutorial/enable-authentication/


## Deploy the Service Broker to Cloud Foundry

The service broker is configured via environment variables, which are defined in the `manifest.yml` file. Make the necessary changes to the MongoDB config in order to connect to your Mongo instance.

Push the service broker as an app to Cloud Foundry:
`cf push`

Register the service broker using the default username and the password obtained from the previous step:
`cf csb mongodb admin admin http://mongodb-service-broker.local.pcfdev.io`

Enable access to the service broker:
`cf enable-service-access mongodb`

Create a service instance:
`cf cs mongodb default mymongodb`
