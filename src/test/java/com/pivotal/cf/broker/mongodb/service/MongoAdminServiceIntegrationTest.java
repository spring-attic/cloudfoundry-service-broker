package com.pivotal.cf.broker.mongodb.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.pivotal.cf.broker.mongodb.MongoConfiguration;
import com.pivotal.cf.broker.mongodb.exception.MongoServiceException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class MongoAdminServiceIntegrationTest {
	
	@Autowired
	private MongoAdminService service;
	
	@Autowired
	private MongoClient client;
	
	@After
	public void cleanup() {
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
	public void newUserCreatedSuccessfully() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		service.createUser(MongoConfiguration.DB_NAME, "user", "password");
		assertTrue(client.getDB(MongoConfiguration.DB_NAME).authenticate("user", "password".toCharArray()));
	}
	
	@Test
	public void deleteUserSucceeds() throws MongoServiceException {
		service.createDatabase(MongoConfiguration.DB_NAME);
		client.getDB(MongoConfiguration.DB_NAME).addUser("user", "password".toCharArray());
		service.deleteUser(MongoConfiguration.DB_NAME, "user");
		assertFalse(client.getDB(MongoConfiguration.DB_NAME).authenticate("user", "password".toCharArray()));	
	}
	
}

