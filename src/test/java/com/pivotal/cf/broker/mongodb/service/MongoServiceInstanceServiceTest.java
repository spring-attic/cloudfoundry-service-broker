package com.pivotal.cf.broker.mongodb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceExistsException;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.fixture.ServiceInstanceFixture;
import com.pivotal.cf.broker.mongodb.MongoConfiguration;
import com.pivotal.cf.broker.mongodb.repository.MongoServiceInstanceRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoServiceInstanceServiceTest {

	private final static String SVC_INST_ID = "serviceInstanceId";
	
	@Autowired
	private MongoClient client;
	
	@Mock
	private MongoAdminService mongo;

	@Mock
	private MongoServiceInstanceRepository repository;
	
	@Mock
	private DB db;

	@Mock
	private ServiceDefinition serviceDefinition;
	
	private MongoServiceInstanceService service;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		service = new MongoServiceInstanceService(mongo, repository);
	}
	
	@After
	public void cleanup() {
		client.dropDatabase(MongoConfiguration.DB_NAME);
	}
	
	@Test
	public void serviceInstancesRetrievedCorrectly() {
		when(repository.findAll()).thenReturn(ServiceInstanceFixture.getAllServiceInstances());
		List<ServiceInstance> instances = service.getAllServiceInstances();
		assertEquals(instances.get(0).getId(), ServiceInstanceFixture.getServiceInstance().getId());
		assertEquals(instances.get(1).getId(), ServiceInstanceFixture.getServiceInstanceTwo().getId());
	}
	
	@Test
	public void newServiceInstanceCreatedSuccessfully() 
			throws ServiceInstanceExistsException, ServiceBrokerException {
		
		when(repository.findOne(any(String.class))).thenReturn(null);
		when(mongo.databaseExists(any(String.class))).thenReturn(false);
		when(mongo.createDatabase(any(String.class))).thenReturn(db);
		
		ServiceInstance instance = service.createServiceInstance(
				serviceDefinition, SVC_INST_ID, "planId", "organizationGuid", "spaceGuid");
		
		assertNotNull(instance);
		assertEquals(SVC_INST_ID, instance.getId());
		
		verify(repository).save(instance);
	}
	
	@Test
	public void newServiceInstanceCreatedSuccessfullyWithExistingDB() 
			throws ServiceInstanceExistsException, ServiceBrokerException {
		
		when(repository.findOne(any(String.class))).thenReturn(null);
		when(mongo.databaseExists(any(String.class))).thenReturn(true);
		when(mongo.createDatabase(any(String.class))).thenReturn(db);
		
		ServiceInstance instance = service.createServiceInstance(
				serviceDefinition, SVC_INST_ID, "planId", "organizationGuid", "spaceGuid");
		
		assertNotNull(instance);
		assertEquals(SVC_INST_ID, instance.getId());
		
		verify(mongo).deleteDatabase(SVC_INST_ID);
		verify(repository).save(instance);		
	}
	
	@Test(expected=ServiceInstanceExistsException.class)
	public void serviceInstanceCreationFailsWithExistingInstance()  
			throws ServiceInstanceExistsException, ServiceBrokerException {
		
		when(repository.findOne(any(String.class))).thenReturn(ServiceInstanceFixture.getServiceInstance());		
		service.createServiceInstance(
				serviceDefinition, SVC_INST_ID, "planId", "organizationGuid", "spaceGuid");
	}
	
	@Test(expected=ServiceBrokerException.class)
	public void serviceInstanceCreationFailsWithDBCreationFailure()  
			throws ServiceInstanceExistsException, ServiceBrokerException {
		when(repository.findOne(any(String.class))).thenReturn(null);
		when(mongo.databaseExists(any(String.class))).thenReturn(false);
		when(mongo.createDatabase(any(String.class))).thenReturn(null);
		
		service.createServiceInstance(
				serviceDefinition, SVC_INST_ID, "planId", "organizationGuid", "spaceGuid");
	}
	
	@Test
	public void successfullyRetrieveServiceInstance() {
		when(repository.findOne(any(String.class))).thenReturn(ServiceInstanceFixture.getServiceInstance());
		String id = ServiceInstanceFixture.getServiceInstance().getId();
		assertEquals(id, service.getServiceInstance(id).getId());
	}
	
	@Test
	public void serviceInstanceDeletedSuccessfully() throws ServiceBrokerException {
		when(repository.findOne(any(String.class))).thenReturn(ServiceInstanceFixture.getServiceInstance());
		String id = ServiceInstanceFixture.getServiceInstance().getId();
		
		assertNotNull(service.deleteServiceInstance(id));
		
		verify(mongo).deleteDatabase(id);
		verify(repository).delete(id);
	}
	
	@Test
	public void unknownServiceInstanceDeleteCallSuccessful() throws ServiceBrokerException {
		when(repository.findOne(any(String.class))).thenReturn(null);
		
		assertNull(service.deleteServiceInstance(SVC_INST_ID));
		
		verify(mongo).deleteDatabase(SVC_INST_ID);
		verify(repository).delete(SVC_INST_ID);
	}
	
}