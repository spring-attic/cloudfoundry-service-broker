package org.springframework.cloud.servicebroker.mongodb.config;

import java.net.UnknownHostException;

import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "org.springframework.cloud.servicebroker.mongodb.repository")
public class MongoConfig {

	@Value("${mongodb.host:localhost}")
	private String host;

	@Value("${mongodb.port:27017}")
	private int port;

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient(new ServerAddress(host, port));
	}
	
}
