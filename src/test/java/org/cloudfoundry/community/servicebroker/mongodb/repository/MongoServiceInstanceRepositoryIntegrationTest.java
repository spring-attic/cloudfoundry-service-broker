package org.cloudfoundry.community.servicebroker.mongodb.repository;

import static org.junit.Assert.assertEquals;

import org.cloudfoundry.community.servicebroker.model.fixture.ServiceInstanceFixture;
import org.cloudfoundry.community.servicebroker.mongodb.MongoConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoServiceInstanceRepositoryIntegrationTest {

	private static final String COLLECTION = "serviceInstance";
	
	@Autowired 
	private MongoClient client;
	
	@Autowired
	MongoServiceInstanceRepository repository;
	
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
	public void instanceInsertedSuccessfully() throws Exception {
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	    repository.save(ServiceInstanceFixture.getServiceInstance());
	    assertEquals(1, mongo.getCollection(COLLECTION).count());
	}

	@Test
	public void instanceDeletedSuccessfully() throws Exception {
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	    repository.save(ServiceInstanceFixture.getServiceInstance());
	    assertEquals(1, mongo.getCollection(COLLECTION).count());
	    repository.delete(ServiceInstanceFixture.getServiceInstance().getId());
	    assertEquals(0, mongo.getCollection(COLLECTION).count());
	}
}