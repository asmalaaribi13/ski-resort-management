package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.SkierServicesImpl;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkierServicesImplTest {

    private static final Logger logger = LoggerFactory.getLogger(SkierServicesImplTest.class);

    // Constants for repeated string literals
    private static final String AGE_GROUP_CHILDREN = "Children (0-12)";
    private static final String AGE_GROUP_TEENS = "Teens (13-19)";
    private static final String AGE_GROUP_ADULTS = "Adults (20-59)";
    private static final String AGE_GROUP_SENIORS = "Seniors (60+)";
    private static final String TEST_SUCCEEDED = "Test succeeded!";

    @InjectMocks
    private SkierServicesImpl skierServices;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAnalyzePisteUsageByAgeGroup() {
        LocalDate currentDate = LocalDate.now();

        Skier skierChild = new Skier(1L, "John", "Doe", currentDate.minusYears(10), "CityA", null, new HashSet<>(), new HashSet<>());
        Skier skierTeen = new Skier(2L, "Jane", "Doe", currentDate.minusYears(15), "CityB", null, new HashSet<>(), new HashSet<>());
        Skier skierAdult = new Skier(3L, "Mike", "Smith", currentDate.minusYears(30), "CityC", null, new HashSet<>(), new HashSet<>());
        Skier skierSenior = new Skier(4L, "Anna", "Jones", currentDate.minusYears(65), "CityD", null, new HashSet<>(), new HashSet<>());

        skierChild.getPistes().add(new Piste(1L, "PisteA", Color.GREEN, 1200, 30, new HashSet<>()));
        skierTeen.getPistes().add(new Piste(2L, "PisteB", Color.BLUE, 800, 20, new HashSet<>()));
        skierAdult.getPistes().add(new Piste(3L, "PisteC", Color.RED, 1500, 25, new HashSet<>()));
        skierSenior.getPistes().add(new Piste(4L, "PisteD", Color.BLACK, 2000, 40, new HashSet<>()));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skierChild, skierTeen, skierAdult, skierSenior));

        Map<String, Double> result = skierServices.analyzePisteUsageByAgeGroup();

        assertEquals(4, result.size(), "The result should contain 4 age groups");

        assertTrue(result.containsKey(AGE_GROUP_CHILDREN), "Group '" + AGE_GROUP_CHILDREN + "' should be present");
        assertEquals(1.0, result.get(AGE_GROUP_CHILDREN), "Piste usage for '" + AGE_GROUP_CHILDREN + "' should be 1.0");

        assertTrue(result.containsKey(AGE_GROUP_TEENS), "Group '" + AGE_GROUP_TEENS + "' should be present");
        assertEquals(1.0, result.get(AGE_GROUP_TEENS), "Piste usage for '" + AGE_GROUP_TEENS + "' should be 1.0");

        assertTrue(result.containsKey(AGE_GROUP_ADULTS), "Group '" + AGE_GROUP_ADULTS + "' should be present");
        assertEquals(1.0, result.get(AGE_GROUP_ADULTS), "Piste usage for '" + AGE_GROUP_ADULTS + "' should be 1.0");

        assertTrue(result.containsKey(AGE_GROUP_SENIORS), "Group '" + AGE_GROUP_SENIORS + "' should be present");
        assertEquals(1.0, result.get(AGE_GROUP_SENIORS), "Piste usage for '" + AGE_GROUP_SENIORS + "' should be 1.0");

        logger.info("testAnalyzePisteUsageByAgeGroup: " + TEST_SUCCEEDED);
    }

    @Test
    void testAnalyzeSkierEngagement() {
        Skier skier1 = new Skier(1L, "John", "Doe", LocalDate.now().minusYears(25), "CityA", null, new HashSet<>(), new HashSet<>());
        Skier skier2 = new Skier(2L, "Jane", "Doe", LocalDate.now().minusYears(30), "CityB", null, new HashSet<>(), new HashSet<>());

        skier1.getRegistrations().add(new Registration(1L, 10, skier1, new Course()));
        skier1.getRegistrations().add(new Registration(2L, 12, skier1, new Course()));
        skier2.getRegistrations().add(new Registration(3L, 15, skier2, new Course()));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));

        Map<String, Object> result = skierServices.analyzeSkierEngagement();

        assertEquals(2, result.size(), "Result size should be 2");
        assertEquals(1.5, result.get("averageCoursesPerSkier"), "Average courses per skier should be 1.5");
        assertEquals(skier1, result.get("mostActiveSkier"), "The most active skier should be skier1");

        logger.info("testAnalyzeSkierEngagement: " + TEST_SUCCEEDED);
    }

    @Test
    void testFindTopSpendingSkiers() {
        Skier skier1 = new Skier(1L, "John", "Doe", LocalDate.now().minusYears(20), "CityA", null, new HashSet<>(), new HashSet<>());
        Skier skier2 = new Skier(2L, "Jane", "Doe", LocalDate.now().minusYears(22), "CityB", null, new HashSet<>(), new HashSet<>());

        Course course1 = new Course(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100f, 2, new HashSet<>());
        Course course2 = new Course(2L, 1, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 200f, 3, new HashSet<>());

        skier1.getRegistrations().add(new Registration(1L, 10, skier1, course1));
        skier1.getRegistrations().add(new Registration(2L, 11, skier1, course2));
        skier2.getRegistrations().add(new Registration(3L, 12, skier2, course1));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier1));
        when(skierRepository.findById(2L)).thenReturn(Optional.of(skier2));

        List<Skier> result = skierServices.findTopSpendingSkiers(1);

        assertEquals(1, result.size(), "The result size should be 1 for top spender");
        assertEquals(skier1, result.get(0), "Top spending skier should be skier1");

        logger.info("testFindTopSpendingSkiers: " + TEST_SUCCEEDED);
    }

    @Test
    void testGetAverageAgeBySubscriptionType() {
        LocalDate today = LocalDate.now();

        Subscription annualSubscription = new Subscription(1L, today.minusYears(1), today.plusYears(1), 300f, TypeSubscription.ANNUAL);
        Subscription monthlySubscription = new Subscription(2L, today.minusMonths(1), today.plusMonths(1), 50f, TypeSubscription.MONTHLY);
        Subscription semiAnnualSubscription = new Subscription(3L, today.minusMonths(6), today.plusMonths(6), 150f, TypeSubscription.SEMESTRIEL);

        Skier skierAnnual1 = new Skier(1L, "John", "Doe", today.minusYears(25), "CityA", annualSubscription, new HashSet<>(), new HashSet<>());
        Skier skierAnnual2 = new Skier(2L, "Alice", "Smith", today.minusYears(35), "CityB", annualSubscription, new HashSet<>(), new HashSet<>());
        Skier skierMonthly = new Skier(3L, "Bob", "Brown", today.minusYears(30), "CityC", monthlySubscription, new HashSet<>(), new HashSet<>());
        Skier skierSemiAnnual = new Skier(4L, "Carol", "White", today.minusYears(40), "CityD", semiAnnualSubscription, new HashSet<>(), new HashSet<>());

        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(Arrays.asList(skierAnnual1, skierAnnual2));
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.MONTHLY)).thenReturn(Collections.singletonList(skierMonthly));
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.SEMESTRIEL)).thenReturn(Collections.singletonList(skierSemiAnnual));

        Map<TypeSubscription, Double> result = skierServices.getAverageAgeBySubscriptionType();

        double expectedAnnualAge = (25 + 35) / 2.0;
        double expectedMonthlyAge = 30.0;
        double expectedSemiAnnualAge = 40.0;

        assertEquals(expectedAnnualAge, result.get(TypeSubscription.ANNUAL), "The average age for ANNUAL subscription should be " + expectedAnnualAge);
        assertEquals(expectedMonthlyAge, result.get(TypeSubscription.MONTHLY), "The average age for MONTHLY subscription should be " + expectedMonthlyAge);
        assertEquals(expectedSemiAnnualAge, result.get(TypeSubscription.SEMESTRIEL), "The average age for SEMESTRIEL subscription should be " + expectedSemiAnnualAge);

        assertEquals(3, result.size(), "The result should contain only 3 subscription types");

        logger.info("testGetAverageAgeBySubscriptionType: " + TEST_SUCCEEDED);
    }

    @Test
    void testCalculateTotalSpendingBySkier() {
        Long skierId = 1L;
        Subscription subscription = new Subscription(1L, LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), 120f, TypeSubscription.ANNUAL);
        Skier skier = new Skier(skierId, "John", "Doe", LocalDate.now().minusYears(28), "CityA", subscription, new HashSet<>(), new HashSet<>());

        Course course1 = new Course(1L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 200f, 3, new HashSet<>());
        Course course2 = new Course(2L, 1, TypeCourse.INDIVIDUAL, Support.SKI, 150f, 2, new HashSet<>());

        skier.getRegistrations().add(new Registration(1L, 10, skier, course1));
        skier.getRegistrations().add(new Registration(2L, 11, skier, course2));

        when(skierRepository.findById(skierId)).thenReturn(Optional.of(skier));

        Float totalSpending = skierServices.calculateTotalSpendingBySkier(skierId);

        assertEquals(470f, totalSpending, "Total spending should be 470");

        logger.info("testCalculateTotalSpendingBySkier: " + TEST_SUCCEEDED);
    }
}
