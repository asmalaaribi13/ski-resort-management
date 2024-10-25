package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.services.SubscriptionServicesImpl;




import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


  class SubscriptionServicesImplMockTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository; // Ajout de la simulation pour skierRepository

    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("Mocks initialized...");
    }

    @Test
     void testAddSubscription() {
        System.out.println("Running testAddSubscription...");
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(500f);

        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        Subscription savedSubscription = subscriptionService.addSubscription(subscription);
        assertEquals(TypeSubscription.ANNUAL, savedSubscription.getTypeSub());

        System.out.println("testAddSubscription passed!");
    }

    @Test
     void testUpdateSubscription() {
        System.out.println("Running testUpdateSubscription...");
        Subscription subscription = new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 500f, TypeSubscription.ANNUAL);
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        Subscription updatedSubscription = subscriptionService.updateSubscription(subscription);
        assertEquals(500f, updatedSubscription.getPrice());

        System.out.println("testUpdateSubscription passed!");
    }

    @Test
     void testRetrieveSubscriptionById() {
        System.out.println("Running testRetrieveSubscriptionById...");
        Subscription subscription = new Subscription(1L, LocalDate.now(), LocalDate.now().plusMonths(1), 100f, TypeSubscription.MONTHLY);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        Subscription retrievedSubscription = subscriptionService.retrieveSubscriptionById(1L);
        assertEquals(1L, retrievedSubscription.getNumSub());

        System.out.println("testRetrieveSubscriptionById passed!");
    }

    @Test
     void testGetSubscriptionByType() {
        System.out.println("Running testGetSubscriptionByType...");
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), 100f, TypeSubscription.MONTHLY)));
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY)).thenReturn(subscriptions);

        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);
        assertEquals(1, result.size());

        System.out.println("testGetSubscriptionByType passed!");
    }

    @Test
    void testRetrieveSubscriptionsByDates() {
        System.out.println("Running testRetrieveSubscriptionsByDates...");
        List<Subscription> subscriptions = Arrays.asList(new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL));
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.retrieveSubscriptionsByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        assertEquals(1, result.size());

        System.out.println("testRetrieveSubscriptionsByDates passed!");
    }

    @Test
    void testDeleteSubscription() {
        System.out.println("Running testDeleteSubscription...");
        Subscription subscription = new Subscription(1L, LocalDate.now(), LocalDate.now().plusMonths(1), 100f, TypeSubscription.MONTHLY);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        subscriptionService.deleteSubscription(1L);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        Subscription deletedSubscription = subscriptionService.retrieveSubscriptionById(1L);
        assertNull(deletedSubscription, "Subscription should be null after deletion");

        System.out.println("testDeleteSubscription passed! Subscription successfully deleted and verified.");
    }

    @Test
     void testGetAllSubscriptions() {
        System.out.println("Running testGetAllSubscriptions...");
        List<Subscription> subscriptions = Arrays.asList(new Subscription(1L, LocalDate.now(), LocalDate.now().plusMonths(1), 100f, TypeSubscription.MONTHLY));
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.getAllSubscriptions();
        assertEquals(1, result.size());

        System.out.println("testGetAllSubscriptions passed! Number of subscriptions: " + result.size());
    }
    @Test
     void testCalculateTotalRevenue() {
        System.out.println("Running testCalculateTotalRevenue...");
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL),
                new Subscription(2L, LocalDate.of(2023, 2, 1), LocalDate.of(2024, 2, 1), 200f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(subscriptions);

        Float totalRevenue = subscriptionService.calculateTotalRevenue(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        assertEquals(300f, totalRevenue);

        System.out.println("testCalculateTotalRevenue passed! Total revenue: " + totalRevenue);
    }

    @Test
     void testFindSubscriptionsExpiringSoon() {
        System.out.println("Running testFindSubscriptionsExpiringSoon...");
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), 100f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.now(), LocalDate.now().plusDays(7)))
                .thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.findSubscriptionsExpiringSoon();
        assertEquals(1, result.size());

        System.out.println("testFindSubscriptionsExpiringSoon passed! Subscriptions expiring soon: " + result.size());
    }

    @Test
     void testCalculateAverageSubscriptionDuration() {
        System.out.println("Running testCalculateAverageSubscriptionDuration...");
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL),
                new Subscription(2L, LocalDate.of(2023, 2, 1), LocalDate.of(2024, 2, 1), 200f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        Float averageDuration = subscriptionService.calculateAverageSubscriptionDuration();
        assertEquals(365f, averageDuration);

        System.out.println("testCalculateAverageSubscriptionDuration passed! Average subscription duration: " + averageDuration + " days");
    }
}