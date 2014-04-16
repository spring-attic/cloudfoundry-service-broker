package com.pivotal.cf.broker.mongodb.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/** 
 * Force the base spring boot packages to be searched for dependencies.
 * 
 * @author sgreenberg
 *
 */

@Configuration
@ComponentScan(basePackages = "com.pivotal.cf.broker")
public class BrokerConfig {

}
