package org.cloudfoundry.community.servicebroker.mongodb.service;

import org.cloudfoundry.community.servicebroker.mongodb.exception.MongoServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * Utility class for manipulating a Mongo database.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongoAdminService {

	public static final String ADMIN_DB = "admin";
	
	private Logger logger = LoggerFactory.getLogger(MongoAdminService.class);
	
	private MongoClient client;
	
	@Autowired
	public MongoAdminService(MongoClient client) {
		this.client = client;
	}
	
	public boolean databaseExists(String databaseName) throws MongoServiceException {
		try {
			return client.getDatabaseNames().contains(databaseName);
		} catch (MongoException e) {
			throw handleException(e);
		}
	}
	
	public void deleteDatabase(String databaseName) throws MongoServiceException {
		try{
			client.getDB(ADMIN_DB);
			client.dropDatabase(databaseName);
		} catch (MongoException e) {
			throw handleException(e);
		}
	}
	
	public DB createDatabase(String databaseName) throws MongoServiceException {
		try {
			DB db = client.getDB(databaseName);
			
			// save into a collection to force DB creation.
			DBCollection col = db.createCollection("foo", null);
			BasicDBObject obj = new BasicDBObject();
			obj.put("foo", "bar");
			col.insert(obj);
			// drop the collection so the db is empty
			col.drop();
			
			return db; 
		} catch (MongoException e) {
			// try to clean up and fail
			try {
				deleteDatabase(databaseName);
			} catch (MongoServiceException ignore) {}
			throw handleException(e);
		}
	}
	
	public void createUser(String database, String username, String password) throws MongoServiceException {
		try {
			DB db = client.getDB(database);
			BasicDBList roles = new BasicDBList();
			roles.add("readWrite");
			DBObject command = BasicDBObjectBuilder.start("createUser", username)
					.add("pwd", password)
					.add("roles", roles)
					.get();
			CommandResult result = db.command(command);
			if (!result.ok()) {
				MongoServiceException e = new MongoServiceException(result.toString());
				logger.warn(e.getLocalizedMessage());
				throw e;
			}
		} catch (MongoException e) {
			throw handleException(e);
		}
	}
	
	public void deleteUser(String database, String username) throws MongoServiceException {
		try {
			DB db = client.getDB(database);
			db.command(new BasicDBObject("dropUser", username));
		} catch (MongoException e) {
			throw handleException(e);
		}
	}
	
	public String getConnectionString(String database, String username, String password) {
		StringBuilder builder = new StringBuilder();
		builder.append("mongodb://");
		builder.append(username);
		builder.append(":");
		builder.append(password);
		builder.append("@");
		builder.append(getServerAddresses());
		builder.append("/");
		builder.append(database);
		return builder.toString();
	}
	
	public String getServerAddresses() {
		StringBuilder builder = new StringBuilder();
		for (ServerAddress address : client.getAllAddress()) {
			builder.append(address.getHost());
			builder.append(":");
			builder.append(address.getPort());
			builder.append(",");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}
	
	private MongoServiceException handleException(Exception e) {
		logger.warn(e.getLocalizedMessage(), e);
		return new MongoServiceException(e.getLocalizedMessage());
	}
	
}
