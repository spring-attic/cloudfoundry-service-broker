package org.springframework.cloud.servicebroker.mongodb;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public abstract class IntegrationTestBase {
	public static final String DB_NAME = "test-mongo-db";
}
