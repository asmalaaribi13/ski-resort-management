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
    private static final String CITY_A = "CityA";
    private static final String CITY_B = "CityB";
    private static final String CITY_C = "CityC";
    private static final String CITY_D = "CityD";

    private static final String LOG_TEST_SUCCESS = "Test succeeded!";
    private static final String ASSERTION_MESSAGE_RESULT_SIZE = "The result size should be ";
    private static final String ASSERTION_MESSAGE_TOTAL_SPENDING = "Total spending should be ";

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

    // Utility method: Create a skier
    private Skier createSkier(Long id, String firstName, String lastName, LocalDate birthDate, String city) {
        return new Skier(id, firstName, lastName, birthDate, city, null, new HashSet<>(), new HashSet<>());
    }

    // Utility method: Create a piste
    private Piste createPiste(Long id, String name, Color color, int length, int slope) {
        return new Piste(id, name, color, length, slope, new HashSet<>());
    }

    @Test
    void testAnalyzePisteUsageByAgeGroup() {
        LocalDate currentDate = LocalDate.now();

        Skier skierChild = createSkier(1L, "John", "Doe", currentDate.minusYears(10), CITY_A);
        Skier skierTeen = createSkier(2L, "Jane", "Doe", currentDate.minusYears(15), CITY_B);
        Skier skierAdult = createSkier(3L, "Mike", "Smith", currentDate.minusYears(30), CITY_C);
        Skier skierSenior = createSkier(4L, "Anna", "Jones", currentDate.minusYears(65), CITY_D);

        skierChild.getPistes().add(createPiste(1L, "PisteA", Color.GREEN, 1200, 30));
        skierTeen.getPistes().add(createPiste(2L, "PisteB", Color.BLUE, 800, 20));
        skierAdult.getPistes().add(createPiste(3L, "PisteC", Color.RED, 1500, 25));
        skierSenior.getPistes().add(createPiste(4L, "PisteD", Color.BLACK, 2000, 40));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skierChild, skierTeen, skierAdult, skierSenior));

        Map<String, Double> result = skierServices.analyzePisteUsageByAgeGroup();

        assertEquals(4, result.size(), ASSERTION_MESSAGE_RESULT_SIZE + "4");
        assertTrue(result.containsKey("Children (0-12)"), "Group 'Children (0-12)' should be present");
        assertEquals(1.0, result.get("Children (0-12)"), "Piste usage for 'Children (0-12)' should be 1.0");
        logger.info("testAnalyzePisteUsageByAgeGroup: " + LOG_TEST_SUCCESS);
    }

    @Test
    void testAnalyzeSkierEngagement() {
        Skier skier1 = createSkier(1L, "John", "Doe", LocalDate.now().minusYears(25), CITY_A);
        Skier skier2 = createSkier(2L, "Jane", "Doe", LocalDate.now().minusYears(30), CITY_B);

        skier1.getRegistrations().add(new Registration(1L, 10, skier1, new Course()));
        skier1.getRegistrations().add(new Registration(2L, 12, skier1, new Course()));
        skier2.getRegistrations().add(new Registration(3L, 15, skier2, new Course()));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));

        Map<String, Object> result = skierServices.analyzeSkierEngagement();

        assertEquals(2, result.size(), ASSERTION_MESSAGE_RESULT_SIZE + "2");
        assertEquals(1.5, result.get("averageCoursesPerSkier"), "Average courses per skier should be 1.5");
        assertEquals(skier1, result.get("mostActiveSkier"), "The most active skier should be skier1");
        logger.info("testAnalyzeSkierEngagement: " + LOG_TEST_SUCCESS);
    }

    @Test
    void testCalculateTotalSpendingBySkier() {
        Long skierId = 1L;
        Skier skier = createSkier(skierId, "John", "Doe", LocalDate.now().minusYears(28), CITY_A);
        skier.setSubscription(new Subscription(1L, LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), 120f, TypeSubscription.ANNUAL));

        when(skierRepository.findById(skierId)).thenReturn(Optional.of(skier));

        Float totalSpending = skierServices.calculateTotalSpendingBySkier(skierId);

        assertEquals(120f, totalSpending, ASSERTION_MESSAGE_TOTAL_SPENDING + "120");
        logger.info("testCalculateTotalSpendingBySkier: " + LOG_TEST_SUCCESS);
    }
}
