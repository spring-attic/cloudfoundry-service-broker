package org.springframework.cloud.servicebroker.mongodb.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoAdminServiceUnitTest {

	private MongoAdminService service;
	
	@Mock
	private MongoClient client;
	
	@Mock
	private ServerAddress server1;
	
	@Mock
	private ServerAddress server2;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new MongoAdminService(client);
	}

	@Test
	public void uriConstructedSuccessfullyWithOneServerAddress() {
		String expected = "mongodb://username:password@server1:1/database";
		
		List<ServerAddress> addresses = new ArrayList<ServerAddress>();
		addresses.add(server1);
		
		when(server1.getHost()).thenReturn("server1");
		when(server1.getPort()).thenReturn(1);	
		when(client.getAllAddress()).thenReturn(addresses);
	
		assertEquals(expected, service.getConnectionString("database", "username", "password"));
	}
	
	@Test
	public void uriConstructedSuccessfullyWithMultipleServerAddresses() {
		String expected = "mongodb://username:password@server1:1,server2:2/database";
		
		List<ServerAddress> addresses = new ArrayList<ServerAddress>();
		addresses.add(server1);
		addresses.add(server2);
		
		when(server1.getHost()).thenReturn("server1");
		when(server1.getPort()).thenReturn(1);	
		when(server2.getHost()).thenReturn("server2");
		when(server2.getPort()).thenReturn(2);	
		when(client.getAllAddress()).thenReturn(addresses);
	
		assertEquals(expected, service.getConnectionString("database", "username", "password"));
	}
	
}
