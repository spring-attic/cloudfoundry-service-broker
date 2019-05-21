package org.springframework.cloud.servicebroker.mongodb.fixture;

import org.springframework.cloud.servicebroker.mongodb.model.ServiceInstance;

public class ServiceInstanceFixture {
	public static ServiceInstance getServiceInstance() {
		return new ServiceInstance("service-instance-id", "service-definition-id", "plan-id",
				"org-guid", "space-guid", "https://dashboard.example.com");
	}
}
