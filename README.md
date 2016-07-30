MongoDB Cloud Foundry Service Broker Example
=======================

Suggested setup for local development:

* [Docker](https://www.docker.com/products/docker)
* [PCFDev](https://network.pivotal.io/products/pcfdev)

For a more permanent deployment, consider deploying mongodb on traditional VMs.

TODO: Example mongodb deployment provisioned using a Vagrantfile

## Deploy MongoDB in a Docker Container
`docker pull mongo`

Start the Docker container:
`docker run -p 27017:27017 -d mongo`


## Deploy MongoDB using Vagrant

TODO - fork this repo: https://github.com/bobthecow/vagrant-mongobox
determine whether the above repo is a good enough starting point, or whether
it's simply better to use Puppet to provision the Vagrant machine with Mongo

## Build the MongoDB Example Service Broker
`git clone https://github.com/dave-malone/cloudfoundry-service-broker`
`cd cloudfoundry-service-broker`

The example service broker has been configured for use with PCFDev. This assumes that your MongoDB instance is routable at `host.pcfdev.io:27017`. Make the necessary changes in `src/main/resources/application.yml` to configure your service broker to use MongoDB deployed elsewhere.

`./gradlew assemble`

The ready to deploy application jar file will now be available under the build/libs directory.

## Deploy the Service Broker to Cloud Foundry

Push the service broker as an app to Cloud Foundry:
`cf push mongodb-service-broker -p build/libs/cloudfoundry-mongodb-service-broker.jar`

Get the default password from the application's logs:
`cf logs mongodb-service-broker --recent | grep default`

Register the service broker using the default username and the password obtained from the previous step:
`cf csb mongodb user password http://mongodb-service-broker.local.pcfdev.io`

Enable access to the service broker:
`cf enable-service-access "Mongo DB"`

Create a service instance:
`cf cs "Mongo DB"  "Default Mongo Plan" mymongodb`


## Deploy sample app which uses a MongoDB service instance

TODO
