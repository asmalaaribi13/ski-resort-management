package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.SubscriptionServicesImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SubscriptionServicesImplTest {

    @Autowired
    private SubscriptionServicesImpl subscriptionService;

    @Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(500f);

        Subscription savedSubscription = subscriptionService.addSubscription(subscription);
        assertNotNull(savedSubscription);
        assertEquals(TypeSubscription.ANNUAL, savedSubscription.getTypeSub());
    }

    @Test
    public void testUpdateSubscription() {
        Subscription subscription = subscriptionService.retrieveSubscriptionById(1L);
        subscription.setPrice(600f);
        Subscription updatedSubscription = subscriptionService.updateSubscription(subscription);
        assertEquals(600f, updatedSubscription.getPrice());
    }

    @Test
    public void testRetrieveSubscriptionById() {
        Subscription subscription = subscriptionService.retrieveSubscriptionById(1L);
        assertNotNull(subscription);
        assertEquals(1L, subscription.getNumSub());
    }

    @Test
    public void testGetSubscriptionByType() {
        Set<Subscription> subscriptions = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);
        assertTrue(subscriptions.size() > 0);
    }

    @Test
    public void testRetrieveSubscriptionsByDates() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        List<Subscription> subscriptions = subscriptionService.retrieveSubscriptionsByDates(startDate, endDate);
        assertTrue(subscriptions.size() > 0);
    }

    @Test
    public void testRetrieveSubscriptions() {
        subscriptionService.retrieveSubscriptions();
        // Log verification can be done with logging frameworks
    }

    @Test
    public void testCalculateTotalRevenue() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Float totalRevenue = subscriptionService.calculateTotalRevenue(startDate, endDate);
        assertTrue(totalRevenue > 0);
    }

    @Test
    public void testFindSubscriptionsExpiringSoon() {
        List<Subscription> subscriptions = subscriptionService.findSubscriptionsExpiringSoon();
        assertTrue(subscriptions.size() > 0);
    }

    @Test
    public void testCalculateAverageSubscriptionDuration() {
        Float averageDuration = subscriptionService.calculateAverageSubscriptionDuration();
        assertTrue(averageDuration > 0);
    }
}
