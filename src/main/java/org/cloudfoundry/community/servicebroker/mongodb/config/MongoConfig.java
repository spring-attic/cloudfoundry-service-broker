package org.cloudfoundry.community.servicebroker.mongodb.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "org.cloudfoundry.community.servicebroker.mongodb.repository")
public class MongoConfig {

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient();
	}
	
}
