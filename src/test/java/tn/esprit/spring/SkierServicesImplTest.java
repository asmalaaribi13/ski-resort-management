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

    // Constantes pour les messages d'assertions et les messages de log
    private static final String AGE_GROUP_ASSERTION = "The result should contain 4 age groups";
    private static final String CHILD_GROUP_ASSERTION = "Group 'Children (0-12)' should be present";
    private static final String TEEN_GROUP_ASSERTION = "Group 'Teens (13-19)' should be present";
    private static final String ADULT_GROUP_ASSERTION = "Group 'Adults (20-59)' should be present";
    private static final String SENIOR_GROUP_ASSERTION = "Group 'Seniors (60+)' should be present";
    private static final String CHILD_USAGE_ASSERTION = "Piste usage for 'Children (0-12)' should be 1.0";
    private static final String TEEN_USAGE_ASSERTION = "Piste usage for 'Teens (13-19)' should be 1.0";
    private static final String ADULT_USAGE_ASSERTION = "Piste usage for 'Adults (20-59)' should be 1.0";
    private static final String SENIOR_USAGE_ASSERTION = "Piste usage for 'Seniors (60+)' should be 1.0";
    private static final String ENGAGEMENT_SIZE_ASSERTION = "Result size should be 2";
    private static final String AVERAGE_COURSES_ASSERTION = "Average courses per skier should be 1.5";
    private static final String MOST_ACTIVE_SKIER_ASSERTION = "The most active skier should be skier1";
    private static final String TOP_SPENDER_ASSERTION = "The result size should be 1 for top spender";
    private static final String TOP_SPENDING_SKIER_ASSERTION = "Top spending skier should be skier1";
    private static final String TOTAL_SPENDING_ASSERTION = "Total spending should be 470";

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

        // Création de skieurs dans chaque groupe d'âge
        Skier skierChild = new Skier(1L, "John", "Doe", currentDate.minusYears(10), "CityA", null, new HashSet<>(), new HashSet<>());
        Skier skierTeen = new Skier(2L, "Jane", "Doe", currentDate.minusYears(15), "CityB", null, new HashSet<>(), new HashSet<>());
        Skier skierAdult = new Skier(3L, "Mike", "Smith", currentDate.minusYears(30), "CityC", null, new HashSet<>(), new HashSet<>());
        Skier skierSenior = new Skier(4L, "Anna", "Jones", currentDate.minusYears(65), "CityD", null, new HashSet<>(), new HashSet<>());

        // Ajout de pistes pour chaque skieur
        skierChild.getPistes().add(new Piste(1L, "PisteA", Color.GREEN, 1200, 30, new HashSet<>()));
        skierTeen.getPistes().add(new Piste(2L, "PisteB", Color.BLUE, 800, 20, new HashSet<>()));
        skierAdult.getPistes().add(new Piste(3L, "PisteC", Color.RED, 1500, 25, new HashSet<>()));
        skierSenior.getPistes().add(new Piste(4L, "PisteD", Color.BLACK, 2000, 40, new HashSet<>()));

        when(skierRepository.findAll()).thenReturn(Arrays.asList(skierChild, skierTeen, skierAdult, skierSenior));

        Map<String, Double> result = skierServices.analyzePisteUsageByAgeGroup();

        // Vérification pour chaque groupe d'âge
        assertEquals(4, result.size(), AGE_GROUP_ASSERTION);
        assertTrue(result.containsKey("Children (0-12)"), CHILD_GROUP_ASSERTION);
        assertEquals(1.0, result.get("Children (0-12)"), CHILD_USAGE_ASSERTION);

        assertTrue(result.containsKey("Teens (13-19)"), TEEN_GROUP_ASSERTION);
        assertEquals(1.0, result.get("Teens (13-19)"), TEEN_USAGE_ASSERTION);

        assertTrue(result.containsKey("Adults (20-59)"), ADULT_GROUP_ASSERTION);
        assertEquals(1.0, result.get("Adults (20-59)"), ADULT_USAGE_ASSERTION);

        assertTrue(result.containsKey("Seniors (60+)"), SENIOR_GROUP_ASSERTION);
        assertEquals(1.0, result.get("Seniors (60+)"), SENIOR_USAGE_ASSERTION);

        logger.info("testAnalyzePisteUsageByAgeGroup: Test succeeded!");
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

        assertEquals(2, result.size(), ENGAGEMENT_SIZE_ASSERTION);
        assertEquals(1.5, result.get("averageCoursesPerSkier"), AVERAGE_COURSES_ASSERTION);
        assertEquals(skier1, result.get("mostActiveSkier"), MOST_ACTIVE_SKIER_ASSERTION);

        logger.info("testAnalyzeSkierEngagement: Test succeeded!");
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

        assertEquals(1, result.size(), TOP_SPENDER_ASSERTION);
        assertEquals(skier1, result.get(0), TOP_SPENDING_SKIER_ASSERTION);

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

        assertEquals(470f, totalSpending, TOTAL_SPENDING_ASSERTION);

        logger.info("testCalculateTotalSpendingBySkier: Test succeeded!");
    }
}
