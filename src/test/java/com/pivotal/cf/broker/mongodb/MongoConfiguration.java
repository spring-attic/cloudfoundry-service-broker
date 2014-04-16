package com.pivotal.cf.broker.mongodb;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.pivotal.cf.broker.mongodb.repository.MongoServiceInstanceBindingRepository;
import com.pivotal.cf.broker.mongodb.repository.MongoServiceInstanceRepository;
import com.pivotal.cf.broker.mongodb.service.MongoAdminService;

@Configuration
@EnableMongoRepositories(basePackages = "com.pivotal.cf.broker.mongodb.repository",
      includeFilters = @ComponentScan.Filter(value = {MongoServiceInstanceRepository.class, MongoServiceInstanceBindingRepository.class}, type = FilterType.ASSIGNABLE_TYPE))
public class MongoConfiguration {

	public static final String DB_NAME = "test-mongo-db";
	
	public @Bean MongoTemplate mongoTemplate(Mongo mongo) throws UnknownHostException {
		return new MongoTemplate(mongo, DB_NAME);
	}

	public @Bean MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient("localhost");
	}
  
	public @Bean MongoAdminService mongoAdminService() throws UnknownHostException {
		return new MongoAdminService(mongoClient());
	}
  
}