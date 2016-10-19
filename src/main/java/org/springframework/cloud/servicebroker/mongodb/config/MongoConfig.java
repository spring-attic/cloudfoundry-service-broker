package org.springframework.cloud.servicebroker.mongodb.config;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(basePackages = "org.springframework.cloud.servicebroker.mongodb.repository")
public class MongoConfig {

	@Value("${mongodb.host:localhost}")
	private String host;

	@Value("${mongodb.port:27017}")
	private int port;

	@Value("${mongodb.username:admin}")
	private String username;

	@Value("${mongodb.password:password}")
	private String password;

	@Value("${mongodb.authdb:admin}")
	private String authSource;

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		final MongoCredential credential = MongoCredential.createScramSha1Credential(username, authSource, password.toCharArray());
		return new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
	}


}
