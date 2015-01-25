package org.cloudfoundry.community.servicebroker.mongodb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceBindingFixture;
import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceFixture;
import org.cloudfoundry.community.servicebroker.mongodb.MongoConfiguration;
import org.cloudfoundry.community.servicebroker.mongodb.exception.MongoServiceException;
import org.cloudfoundry.community.servicebroker.mongodb.repository.MongoServiceInstanceBindingRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoServiceInstanceBindingServiceTest {
	
	@Autowired
	private MongoClient client;
	
	@Mock
	private MongoAdminService mongo;

	@Mock
	private MongoServiceInstanceBindingRepository repository;
	
	private MongoServiceInstanceBindingService service;
	
	private ServiceInstance instance;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new MongoServiceInstanceBindingService(mongo, repository);
		instance = ServiceInstanceFixture.getServiceInstance();	
	}
	
	@After
	public void cleanup() {
		client.dropDatabase(MongoConfiguration.DB_NAME);
	}
	
	// TODO Test if user already exists
	
	@Test
	public void newServiceInstanceBindingCreatedSuccessfully() 
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		
		when(repository.findOne(any(String.class))).thenReturn(null);
		//when(mongo.createUser(any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		String bindingId = "bindingId";
		
		ServiceInstanceBinding binding = service.createServiceInstanceBinding(bindingId, instance, 
				instance.getServiceDefinitionId(), instance.getPlanId(), "app_guid");
		
		assertNotNull(binding);
		assertEquals(bindingId, binding.getId());
		
		verify(repository).save(binding);
	}
	
	@Test(expected=ServiceInstanceBindingExistsException.class)
	public void serviceInstanceCreationFailsWithExistingInstance()  
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		
		when(repository.findOne(any(String.class)))
				.thenReturn(ServiceInstanceBindingFixture.getServiceInstanceBinding());
		
		service.createServiceInstanceBinding("bindingId", instance, 
				instance.getServiceDefinitionId(), instance.getPlanId(), "app_guid");
	}

	@Test(expected=ServiceBrokerException.class)
	public void serviceInstanceBindingCreationFailsWithUserCreationFailure()  
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		when(repository.findOne(any(String.class))).thenReturn(null);
		doThrow(new MongoServiceException("fail")).when(mongo).createUser(any(String.class), any(String.class), any(String.class));
		
		service.createServiceInstanceBinding("bindingId", instance, 
				instance.getServiceDefinitionId(), instance.getPlanId(), "app_guid");
	}
		
	@Test
	public void successfullyRetrieveServiceInstanceBinding() {
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		when(repository.findOne(any(String.class))).thenReturn(binding);
		
		assertEquals(binding.getId(), service.getServiceInstanceBinding(binding.getId()).getId());
	}
	
	@Test
	public void serviceInstanceBindingDeletedSuccessfully() throws ServiceBrokerException {
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		when(repository.findOne(any(String.class))).thenReturn(binding);

		assertNotNull(service.deleteServiceInstanceBinding(binding.getId(), null, null, null));
		
		verify(mongo).deleteUser(binding.getServiceInstanceId(), binding.getId());
		verify(repository).delete(binding.getId());
	}
	
	@Test
	public void unknownServiceInstanceDeleteCallSuccessful() throws ServiceBrokerException {
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		
		when(repository.findOne(any(String.class))).thenReturn(null);
		
		assertNull(service.deleteServiceInstanceBinding(binding.getId(), null, null, null));
		
		verify(mongo, never()).deleteUser(binding.getServiceInstanceId(), binding.getId());
		verify(repository, never()).delete(binding.getId());
	}
	
}
