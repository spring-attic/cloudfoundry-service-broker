package org.springframework.cloud.servicebroker.mongodb.repository;

import org.springframework.cloud.servicebroker.mongodb.model.ServiceInstanceBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for ServiceInstanceBinding objects
 * 
 * @author sgreenberg@pivotal.io
 *
 */
public interface MongoServiceInstanceBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

}
