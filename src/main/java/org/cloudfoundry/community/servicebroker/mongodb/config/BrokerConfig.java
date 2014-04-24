package org.cloudfoundry.community.servicebroker.mongodb.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/** 
 * Force the base spring boot packages to be searched for dependencies.
 * 
 * @author sgreenberg
 *
 */

@Configuration
@ComponentScan(basePackages = "org.cloudfoundry.community.servicebroker")
public class BrokerConfig {

}
