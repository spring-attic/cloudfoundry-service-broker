package com.pivotal.cf.broker.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pivotal.cf.broker.model.ServiceInstance;

/**
 * Repository for ServiceInstance objects
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public interface MongoServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {

}