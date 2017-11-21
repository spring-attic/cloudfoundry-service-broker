## service-broker-mongodb

## Installation and Usage for Kubernetes

PREREQUISITES:
- Kubernetes is up and running
- The service catalog is deployed on Kubernetes

### Deploy and configure a mongodb server
Example using helm chart from here: https://github.com/kubernetes/charts/tree/master/stable/mongodb

1. Deploy the mongodb server using helm.  You can also configure a DB instance in this step but it is not needed.
~~~
helm install --name mongodb --set mongodbRootPassword=password charts/mongodb
~~~

2. Verify that the pod is started in Kubernetes UI and there are no errors.

3. Log into the mongo database server
~~~
sudo docker run -it --rm bitnami/mongodb mongo --host 172.17.0.7 admin -u root -p password
~~~


###  Create an account on the database server for the service broker

1. Connect to the database server
2. Add the user that will create databases
~~~
use admin
db.createUser({ user: 'admin', pwd: 'password', roles: [{"role" : "readWriteAnyDatabase","db" : "admin"},{"role" : "userAdminAnyDatabase","db" : "admin"}] });
~~~

### Deploy the mongodb-service-broker into Kubernetes

1. Install the mongodb-service-broker into kubernetes
~~~
helm install charts/service-broker-mongodb --name service-broker-mongodb --namespace default
~~~
2. Verify that the pod is started in kubernetes UI

3. Verify Successful installation of the broker
- a. list out all of the brokers and classes.  It may take a few minutes, but there should be two entries, one for the broker, and one for the service class
~~~
kubectl get clusterservicebrokers,clusterserviceclasses
~~~
- b. If the service class doesn't show up, look at the error message
~~~
kubectl get clusterservicebrokers mongodb-service-broker -o yaml
~~~

4, Show the plans
~~~
kubectl get clusterserviceplans -o=custom-columns=NAME:.metadata.name,EXTERNAL\ NAME:.spec.externalName
~~~

### Provisioning a new DB instance using the service catalog
~~~
kubectl create -f charts/service-broker-mongodb/mongodb-service-instance.yaml
~~~

### Binding the DB to a service

~~~
kubectl create -f charts/service-broker-mongodb/mongodb-service-binding.yaml
~~~