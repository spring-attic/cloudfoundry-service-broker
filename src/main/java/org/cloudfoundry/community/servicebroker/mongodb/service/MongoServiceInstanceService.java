package org.cloudfoundry.community.servicebroker.mongodb.service;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
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
	public ServiceInstance deleteServiceInstance(String id, String serviceId, String planId) throws MongoServiceException {
		mongo.deleteDatabase(id);
		ServiceInstance instance = repository.findOne(id);
		repository.delete(id);
		return instance;		
	}

	@Override
	public ServiceInstance updateServiceInstance(String instanceId, String planId)
			throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
		ServiceInstance instance = repository.findOne(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		repository.delete(instanceId);
		ServiceInstance updatedInstance = new ServiceInstance(instanceId, 
				instance.getServiceDefinitionId(), planId, instance.getOrganizationGuid(), 
				instance.getSpaceGuid(), instance.getDashboardUrl());
		repository.save(updatedInstance);
		return updatedInstance;
	}

}