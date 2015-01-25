package org.cloudfoundry.community.servicebroker.mongodb.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cloudfoundry.community.servicebroker.mongodb.MongoConfiguration;
import org.cloudfoundry.community.servicebroker.mongodb.exception.MongoServiceException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoAdminServiceIntegrationTest {
	
	@Autowired
	private MongoAdminService service;
	
	@Autowired
	private MongoClient client;
	
	@After
	public void cleanup() {
		client.getDB(MongoConfiguration.DB_NAME).command("dropAllUsersFromDatabase");
		client.dropDatabase(MongoConfiguration.DB_NAME);
	}
	
	@Test
	public void instanceCreationIsSuccessful() throws MongoServiceException {
		DB db = service.createDatabase(MongoConfiguration.DB_NAME);
		assertTrue(client.getDatabaseNames().contains(MongoConfiguration.DB_NAME));
		assertNotNull(db);
	}
	
	@Test
	public void databaseNameDoesNotExist() throws MongoServiceException {
		assertFalse(service.databaseExists("NOT_HERE"));
	}
	
	@Test
	public void databaseNameExists() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		assertTrue(service.databaseExists(MongoConfiguration.DB_NAME));
	}
	
	@Test
	public void deleteDatabaseSucceeds() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		assertTrue(client.getDatabaseNames().contains(MongoConfiguration.DB_NAME));
		service.deleteDatabase(MongoConfiguration.DB_NAME);
		assertFalse(client.getDatabaseNames().contains(MongoConfiguration.DB_NAME));
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void newUserCreatedSuccessfully() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		service.createUser(MongoConfiguration.DB_NAME, "user", "password");
		assertTrue(client.getDB(MongoConfiguration.DB_NAME).authenticate("user", "password".toCharArray()));
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void deleteUserSucceeds() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		DBObject createUserCmd = BasicDBObjectBuilder.start("createUser", "user").add("pwd", "password")
				.add("roles", new BasicDBList()).get();
		CommandResult result = client.getDB(MongoConfiguration.DB_NAME).command(createUserCmd);
		assertTrue("create should succeed", result.ok());
		service.deleteUser(MongoConfiguration.DB_NAME, "user");
		assertFalse(client.getDB(MongoConfiguration.DB_NAME).authenticate("user", "password".toCharArray()));	
	}
	
}

