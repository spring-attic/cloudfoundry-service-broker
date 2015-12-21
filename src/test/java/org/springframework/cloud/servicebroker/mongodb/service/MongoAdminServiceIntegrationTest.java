package org.springframework.cloud.servicebroker.mongodb.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.mongodb.IntegrationTestBase;
import org.springframework.cloud.servicebroker.mongodb.exception.MongoServiceException;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class MongoAdminServiceIntegrationTest extends IntegrationTestBase {
	
	@Autowired
	private MongoAdminService service;
	
	@Autowired
	private MongoClient client;
	
	@After
	public void cleanup() {
		client.getDB(DB_NAME).command("dropAllUsersFromDatabase");
		client.dropDatabase(DB_NAME);
	}
	
	@Test
	public void instanceCreationIsSuccessful() throws MongoServiceException {
		DB db = service.createDatabase(DB_NAME);
		assertTrue(client.getDatabaseNames().contains(DB_NAME));
		assertNotNull(db);
	}
	
	@Test
	public void databaseNameDoesNotExist() throws MongoServiceException {
		assertFalse(service.databaseExists("NOT_HERE"));
	}
	
	@Test
	public void databaseNameExists() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		assertTrue(service.databaseExists(DB_NAME));
	}
	
	@Test
	public void deleteDatabaseSucceeds() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		assertTrue(client.getDatabaseNames().contains(DB_NAME));
		service.deleteDatabase(DB_NAME);
		assertFalse(client.getDatabaseNames().contains(DB_NAME));
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void newUserCreatedSuccessfully() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		service.createUser(DB_NAME, "user", "password");
		assertTrue(client.getDB(DB_NAME).authenticate("user", "password".toCharArray()));
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void deleteUserSucceeds() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		DBObject createUserCmd = BasicDBObjectBuilder.start("createUser", "user").add("pwd", "password")
				.add("roles", new BasicDBList()).get();
		CommandResult result = client.getDB(DB_NAME).command(createUserCmd);
		assertTrue("create should succeed", result.ok());
		service.deleteUser(DB_NAME, "user");
		assertFalse(client.getDB(DB_NAME).authenticate("user", "password".toCharArray()));
	}
	
}

