package org.cloudfoundry.community.servicebroker.mongodb.service;

import java.util.List;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.mongodb.exception.MongoServiceException;
import org.cloudfoundry.community.servicebroker.mongodb.repository.MongoServiceInstanceRepository;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DB;

/**
 * Mongo impl to manage service instances.  Creating a service does the following:
 * creates a new database,
 * saves the ServiceInstance info to the Mongo repository.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongoServiceInstanceService implements ServiceInstanceService {

	private MongoAdminService mongo;
	
	private MongoServiceInstanceRepository repository;
	
	@Autowired
	public MongoServiceInstanceService(MongoAdminService mongo, MongoServiceInstanceRepository repository) {
		this.mongo = mongo;
		this.repository = repository;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		return repository.findAll();
	}

	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) 
			throws ServiceInstanceExistsException, ServiceBrokerException {
		// TODO MongoDB dashboard
		ServiceInstance instance = repository.findOne(serviceInstanceId);
		if (instance != null) {
			throw new ServiceInstanceExistsException(instance);
		}
		instance = new ServiceInstance(serviceInstanceId, service.getId(),
				planId, organizationGuid, spaceGuid, null);
		if (mongo.databaseExists(instance.getId())) {
			// ensure the instance is empty
			mongo.deleteDatabase(instance.getId());
		}
		DB db = mongo.createDatabase(instance.getId());
		if (db == null) {
			throw new ServiceBrokerException("Failed to create new DB instance: " + instance.getId());
		}
		repository.save(instance);
		return instance;
	}
	

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return repository.findOne(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) throws MongoServiceException {
		mongo.deleteDatabase(id);
		ServiceInstance instance = repository.findOne(id);
		repository.delete(id);
		return instance;		
	}

}