package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.services.SubscriptionServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class SubscriptionServicesImplMockTest {

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ISkierRepository skierRepository; // Ajout de la simulation pour skierRepository

    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(500f);

        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        Subscription savedSubscription = subscriptionService.addSubscription(subscription);
        assertEquals(TypeSubscription.ANNUAL, savedSubscription.getTypeSub());
    }

    @Test
    public void testUpdateSubscription() {
        Subscription subscription = new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 500f, TypeSubscription.ANNUAL);
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        Subscription updatedSubscription = subscriptionService.updateSubscription(subscription);
        assertEquals(500f, updatedSubscription.getPrice());
    }

    @Test
    public void testRetrieveSubscriptionById() {
        Subscription subscription = new Subscription(1L, LocalDate.now(), LocalDate.now().plusMonths(1), 100f, TypeSubscription.MONTHLY);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        Subscription retrievedSubscription = subscriptionService.retrieveSubscriptionById(1L);
        assertEquals(1L, retrievedSubscription.getNumSub());
    }

    @Test
    public void testGetSubscriptionByType() {
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), 100f, TypeSubscription.MONTHLY)));
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY)).thenReturn(subscriptions);

        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);
        assertEquals(1, result.size());
    }

    @Test
    public void testRetrieveSubscriptionsByDates() {
        List<Subscription> subscriptions = Arrays.asList(new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL));
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.retrieveSubscriptionsByDates(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        assertEquals(1, result.size());
    }



    @Test
    public void testCalculateTotalRevenue() {
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL),
                new Subscription(2L, LocalDate.of(2023, 2, 1), LocalDate.of(2024, 2, 1), 200f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(subscriptions);

        Float totalRevenue = subscriptionService.calculateTotalRevenue(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        assertEquals(300f, totalRevenue);
    }

    @Test
    public void testFindSubscriptionsExpiringSoon() {
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), 100f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(LocalDate.now(), LocalDate.now().plusDays(7)))
                .thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.findSubscriptionsExpiringSoon();
        assertEquals(1, result.size());
    }

    @Test
    public void testCalculateAverageSubscriptionDuration() {
        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(1L, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), 100f, TypeSubscription.ANNUAL),
                new Subscription(2L, LocalDate.of(2023, 2, 1), LocalDate.of(2024, 2, 1), 200f, TypeSubscription.ANNUAL)
        );
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        Float averageDuration = subscriptionService.calculateAverageSubscriptionDuration();
        assertEquals(365f, averageDuration);
    }
}