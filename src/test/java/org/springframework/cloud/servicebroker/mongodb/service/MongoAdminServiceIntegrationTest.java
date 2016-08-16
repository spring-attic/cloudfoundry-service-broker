package org.springframework.cloud.servicebroker.mongodb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.mongodb.IntegrationTestBase;
import org.springframework.cloud.servicebroker.mongodb.exception.MongoServiceException;
import org.springframework.test.annotation.DirtiesContext;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


public class MongoAdminServiceIntegrationTest extends IntegrationTestBase {
	
	@Autowired
	private MongoAdminService service;
	
	@Autowired
	private MongoClient client;
	
	@After
	public void cleanup() {
		client.getDatabase(DB_NAME).runCommand(new Document("dropAllUsersFromDatabase", 1));
		client.dropDatabase(DB_NAME);
	}
	
	@Test
	public void instanceCreationIsSuccessful() throws MongoServiceException {
		MongoDatabase db = service.createDatabase(DB_NAME);
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
		assertNotNull(client.getDatabase(DB_NAME));
		service.deleteDatabase(DB_NAME);
		
		boolean dbExists = false;
		for(String dbname : client.listDatabaseNames()){
			if(dbname.equals(DB_NAME)){
				dbExists = true;
				break;
			}
		}
		assertFalse(dbExists);
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void newUserCreatedSuccessfully() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		service.createUser(DB_NAME, "user", "password");
		
		BasicDBObject userInfoCmd = new BasicDBObject("usersInfo", "user");
		Document result = client.getDatabase(DB_NAME).runCommand(userInfoCmd);
		List users = (List) result.get("users");
		assertFalse("create should succeed", users.isEmpty());
		
		Document userDoc = (Document) users.get(0);
		
		assertEquals("user list should contain the 'user' user", "user", userDoc.get("user"));
	}
	
	@Test
	@DirtiesContext // because we can't authenticate twice on same DB
	public void deleteUserSucceeds() throws MongoServiceException {
		service.createDatabase(DB_NAME);
		
		Map<String, Object> commandArguments = new BasicDBObject();
	    commandArguments.put("createUser", "user");
	    commandArguments.put("pwd", "password");
	   
	    commandArguments.put("roles", new BasicDBList());
	    BasicDBObject createUserCmd = new BasicDBObject(commandArguments);
		
		Document result = client.getDatabase(DB_NAME).runCommand(createUserCmd);
		assertEquals("create should succeed", result.getDouble("ok"), new Double(1.0d));
		service.deleteUser(DB_NAME, "user");
		
		BasicDBObject userInfoCmd = new BasicDBObject("usersInfo", "user");
		result = client.getDatabase(DB_NAME).runCommand(userInfoCmd);
		List users = (List) result.get("users");
		assertTrue("create should succeed", users.isEmpty());
	}
	
}

