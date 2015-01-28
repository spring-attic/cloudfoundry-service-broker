package org.cloudfoundry.community.servicebroker.mongodb.service;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.mongodb.repository.MongoServiceInstanceBindingRepository;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	protected ServiceInstanceBinding getServiceInstanceBinding(String id) {
		return repository.findOne(id);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(
			String bindingId, ServiceInstance instance, 
			String serviceId, String planId)
			throws ServiceBrokerException {
		ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);
		if (binding!= null) {
			mongo.deleteUser(binding.getServiceInstanceId(), bindingId);
			repository.delete(bindingId);
		}
		return binding;
	}

}
