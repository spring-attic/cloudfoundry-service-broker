package com.pivotal.cf.broker.mongodb.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingExistsException;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.mongodb.exception.MongoServiceException;
import com.pivotal.cf.broker.mongodb.repository.MongoServiceInstanceBindingRepository;
import com.pivotal.cf.broker.service.ServiceInstanceBindingService;

/**
 * Mongo impl to bind services.  Binding a service does the following:
 * creates a new user in the database (currently uses a default pwd of "password"),
 * saves the ServiceInstanceBinding info to the Mongo repository.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongoServiceInstanceBindingService implements ServiceInstanceBindingService {

	private MongoAdminService mongo; 
	
	private MongoServiceInstanceBindingRepository repository;
	
	@Autowired
	public MongoServiceInstanceBindingService(MongoAdminService mongo, 
			MongoServiceInstanceBindingRepository repository) {
		this.mongo = mongo;
		this.repository = repository;
	}
	
	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			String bindingId, ServiceInstance serviceInstance,
			String serviceId, String planId, String appGuid)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		
		ServiceInstanceBinding binding = repository.findOne(bindingId);
		if (binding != null) {
			throw new ServiceInstanceBindingExistsException(binding);
		}
		
		String database = serviceInstance.getId();
		String username = bindingId;
		// TODO Password Generator
		String password = "password";
		
		// TODO check if user already exists in the DB

		mongo.createUser(database, username, password);
		
		Map<String,Object> credentials = new HashMap<String,Object>();
		credentials.put("uri", mongo.getConnectionString(database, username, password));
		
		binding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null, appGuid);
		repository.save(binding);
		
		return binding;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		return repository.findOne(id);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(String id) throws MongoServiceException {
		ServiceInstanceBinding binding = getServiceInstanceBinding(id);
		if (binding!= null) { 
			mongo.deleteUser(binding.getServiceInstanceId(), id);
			repository.delete(id);
		}
		return binding;
	}

}
