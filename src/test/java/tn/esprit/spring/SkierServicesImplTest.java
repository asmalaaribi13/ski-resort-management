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
    private static final String SHOULD_BE_PRESENT = " should be present";
    private static final String PISTE_USAGE_FOR = "Piste usage for '";
    private static final String SHOULD_BE = "' should be 1.0";
    private static final String RESULT_SIZE_MESSAGE = "The result size should be ";
    private static final String TOTAL_SPENDING = "Total spending should be ";

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

        assertEquals(4, result.size(), RESULT_SIZE_MESSAGE + "4");

        assertTrue(result.containsKey(AGE_GROUP_CHILDREN), "Group '" + AGE_GROUP_CHILDREN + SHOULD_BE_PRESENT);
        assertEquals(1.0, result.get(AGE_GROUP_CHILDREN), PISTE_USAGE_FOR + AGE_GROUP_CHILDREN + SHOULD_BE);

        assertTrue(result.containsKey(AGE_GROUP_TEENS), "Group '" + AGE_GROUP_TEENS + SHOULD_BE_PRESENT);
        assertEquals(1.0, result.get(AGE_GROUP_TEENS), PISTE_USAGE_FOR + AGE_GROUP_TEENS + SHOULD_BE);

        assertTrue(result.containsKey(AGE_GROUP_ADULTS), "Group '" + AGE_GROUP_ADULTS + SHOULD_BE_PRESENT);
        assertEquals(1.0, result.get(AGE_GROUP_ADULTS), PISTE_USAGE_FOR + AGE_GROUP_ADULTS + SHOULD_BE);

        assertTrue(result.containsKey(AGE_GROUP_SENIORS), "Group '" + AGE_GROUP_SENIORS + SHOULD_BE_PRESENT);
        assertEquals(1.0, result.get(AGE_GROUP_SENIORS), PISTE_USAGE_FOR + AGE_GROUP_SENIORS + SHOULD_BE);

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

        assertEquals(2, result.size(), RESULT_SIZE_MESSAGE + "2");
        assertEquals(1.5, result.get("averageCoursesPerSkier"), "Average courses per skier should be 1.5");
        assertEquals(skier1, result.get("mostActiveSkier"), "The most active skier should be skier1");

        logger.info("testAnalyzeSkierEngagement: " + TEST_SUCCEEDED);
    }

    @Test
    void testFindTopSpendingSkiers() {
        // Créer les entités de test
        Skier skier1 = new Skier(1L, "John", "Doe", LocalDate.now().minusYears(20), "CityA",
                new Subscription(1L, LocalDate.now(), LocalDate.now().plusMonths(1), 50f, TypeSubscription.MONTHLY),
                new HashSet<>(), new HashSet<>());

        Skier skier2 = new Skier(2L, "Jane", "Doe", LocalDate.now().minusYears(22), "CityB",
                new Subscription(2L, LocalDate.now(), LocalDate.now().plusMonths(6), 100f, TypeSubscription.SEMESTRIEL),
                new HashSet<>(), new HashSet<>());

        Course course1 = new Course(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100f, 2, new HashSet<>());
        Course course2 = new Course(2L, 1, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 200f, 3, new HashSet<>());

        skier1.getRegistrations().add(new Registration(1L, 10, skier1, course1));
        skier1.getRegistrations().add(new Registration(2L, 11, skier1, course2));
        skier2.getRegistrations().add(new Registration(3L, 12, skier2, course1));

        // Simuler les appels au repository
        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier1));
        when(skierRepository.findById(2L)).thenReturn(Optional.of(skier2));

        // Exécuter la méthode à tester
        List<Skier> result = skierServices.findTopSpendingSkiers(1);

        // Vérifications
        assertEquals(1, result.size(), "The result should contain exactly 1 skier");
        assertEquals(skier1, result.get(0), "Top spending skier should be skier1");

        logger.info("testFindTopSpendingSkiers: Test succeeded!");
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

        assertEquals(470f, totalSpending, TOTAL_SPENDING + "470");

        logger.info("testCalculateTotalSpendingBySkier: " + TEST_SUCCEEDED);
    }

    @Test
    void testGetAverageAgeBySubscriptionType() {
        // Données de test
        LocalDate today = LocalDate.now();

        // Skieurs pour chaque type d'abonnement
        List<Skier> annualSkiers = Arrays.asList(
                new Skier(1L, "John", "Doe", today.minusYears(25), "CityA", null, new HashSet<>(), new HashSet<>()),
                new Skier(2L, "Jane", "Smith", today.minusYears(30), "CityB", null, new HashSet<>(), new HashSet<>())
        );

        List<Skier> monthlySkiers = Arrays.asList(
                new Skier(3L, "Alice", "Brown", today.minusYears(20), "CityC", null, new HashSet<>(), new HashSet<>()),
                new Skier(4L, "Bob", "White", today.minusYears(22), "CityD", null, new HashSet<>(), new HashSet<>())
        );

        List<Skier> semestrialSkiers = Arrays.asList(
                new Skier(5L, "Charlie", "Black", today.minusYears(35), "CityE", null, new HashSet<>(), new HashSet<>())
        );

        // Simulation des appels au repository
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(annualSkiers);
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.MONTHLY)).thenReturn(monthlySkiers);
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.SEMESTRIEL)).thenReturn(semestrialSkiers);

        // Appel de la méthode à tester
        Map<TypeSubscription, Double> result = skierServices.getAverageAgeBySubscriptionType();

        // Calculs attendus
        double expectedAnnualAverage = (25 + 30) / 2.0;
        double expectedMonthlyAverage = (20 + 22) / 2.0;
        double expectedSemestrialAverage = 35.0;

        // Assertions
        assertEquals(expectedAnnualAverage, result.get(TypeSubscription.ANNUAL), "Average age for ANNUAL should match");
        assertEquals(expectedMonthlyAverage, result.get(TypeSubscription.MONTHLY), "Average age for MONTHLY should match");
        assertEquals(expectedSemestrialAverage, result.get(TypeSubscription.SEMESTRIEL), "Average age for SEMESTRIEL should match");

        // Vérification des appels
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.MONTHLY);
        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.SEMESTRIEL);

        logger.info("Test passed: testGetAverageAgeBySubscriptionType");
    }
}
