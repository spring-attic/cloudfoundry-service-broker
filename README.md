MongoDB Cloud Foundry Service Broker Example
=======================

Suggested setup for local development:

* [MongoDB instance](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/)
* [PCFDev](https://network.pivotal.io/products/pcfdev)


## Enable Auth in your MongoDB instance

Add the Initial Admin User:
```
$ mongo
> db.createUser({ user: 'admin', pwd: 'password', roles: [{"role" : "readWriteAnyDatabase","db" : "admin"},{"role" : "userAdminAnyDatabase","db" : "admin"}] });
```

Update your mongod.conf file to enable auth. For example: https://gist.github.com/dave-malone/31e35b80004681b84755a6a9ba46c9ae

Restart your Mongo service. 

Test that authentication is working as expected: 

`mongo --authenticationDatabase "admin" -u "admin" -p "password"`

Refer to the MongoDB docs for more details: https://docs.mongodb.com/manual/tutorial/enable-authentication/

## Build the MongoDB Example Service Broker

```
git clone https://github.com/dave-malone/cloudfoundry-service-broker
cd cloudfoundry-service-broker
./gradlew assemble
```

## Deploy the Service Broker to Cloud Foundry

The service broker is configured via environment variables, which are defined in the manifest.yml file. Make the necessary changes to the MongoDB config in order to connect to your Mongo instance.


Push the service broker as an app to Cloud Foundry:
`cf push`

Register the service broker using the default username and the password obtained from the previous step:
`cf csb mongodb admin admin http://mongodb-service-broker.local.pcfdev.io`

Enable access to the service broker:
`cf enable-service-access mongodb`

Create a service instance:
`cf cs mongodb default mymongodb`
