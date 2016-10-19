package org.springframework.cloud.servicebroker.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.mongodb.exception.MongoServiceException;
import org.springframework.cloud.servicebroker.mongodb.model.ServiceInstance;
import org.springframework.cloud.servicebroker.mongodb.repository.MongoServiceInstanceRepository;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;

/**
 * Mongo impl to manage service instances.  Creating a service does the following:
 * creates a new database,
 * saves the ServiceInstance info to the Mongo repository.
 *  
 * @author sgreenberg@pivotal.io
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
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		// TODO MongoDB dashboard
		ServiceInstance instance = repository.findOne(request.getServiceInstanceId());
		if (instance != null) {
			throw new ServiceInstanceExistsException(request.getServiceInstanceId(), request.getServiceDefinitionId());
		}

		instance = new ServiceInstance(request);

		if (mongo.databaseExists(instance.getServiceInstanceId())) {
			// ensure the instance is empty
			mongo.deleteDatabase(instance.getServiceInstanceId());
		}

		MongoDatabase db = mongo.createDatabase(instance.getServiceInstanceId());
		if (db == null) {
			throw new ServiceBrokerException("Failed to create new DB instance: " + instance.getServiceInstanceId());
		}
		repository.save(instance);

		return new CreateServiceInstanceResponse();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
	}

	public ServiceInstance getServiceInstance(String id) {
		return repository.findOne(id);
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) throws MongoServiceException {
		String instanceId = request.getServiceInstanceId();
		ServiceInstance instance = repository.findOne(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		mongo.deleteDatabase(instanceId);
		repository.delete(instanceId);
		return new DeleteServiceInstanceResponse();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
		String instanceId = request.getServiceInstanceId();
		ServiceInstance instance = repository.findOne(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		repository.delete(instanceId);
		ServiceInstance updatedInstance = new ServiceInstance(request);
		repository.save(updatedInstance);
		return new UpdateServiceInstanceResponse();
	}

}