package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.services.SubscriptionServicesImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GestionStationSkiApplicationTests {

	@Autowired
	private SubscriptionServicesImpl subscriptionService;

	@Test
	void contextLoads() {
	}

	@Test
	void testSubscriptionServiceBeanLoaded() {
		// Vérifie que le service Subscription est bien chargé dans le contexte
		assertNotNull(subscriptionService);
	}

}
