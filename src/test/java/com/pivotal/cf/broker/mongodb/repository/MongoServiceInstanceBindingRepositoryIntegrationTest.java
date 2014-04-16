package com.pivotal.cf.broker.mongodb.repository;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.MongoClient;
import com.pivotal.cf.broker.model.fixture.ServiceInstanceBindingFixture;
import com.pivotal.cf.broker.mongodb.MongoConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoServiceInstanceBindingRepositoryIntegrationTest {

	private static final String COLLECTION = "serviceInstanceBinding";
	
	@Autowired 
	private MongoClient client;
	
	@Autowired
	MongoServiceInstanceBindingRepository repository;
	
	@Autowired
	MongoOperations mongo;
	
	@Before
	public void setup() throws Exception {
		mongo.dropCollection(COLLECTION);
	}
	
	@After
	public void teardown() {
	    mongo.dropCollection(COLLECTION);
	    client.dropDatabase(MongoConfiguration.DB_NAME);
	}
	
	@Test
	public void bindingInsertedSuccessfully() throws Exception {
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	    repository.save(ServiceInstanceBindingFixture.getServiceInstanceBinding());
	    assertEquals(1, mongo.getCollection(COLLECTION).count());
	}

	@Test
	public void bindingDeletedSuccessfully() throws Exception {
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	    repository.save(ServiceInstanceBindingFixture.getServiceInstanceBinding());
	    assertEquals(1, mongo.getCollection(COLLECTION).count());
	    repository.delete(ServiceInstanceBindingFixture.getServiceInstanceBinding().getId());
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	}
}