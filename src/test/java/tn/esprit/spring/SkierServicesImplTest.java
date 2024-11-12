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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkierServicesImplTest {

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

    // Test for analyzePisteUsageByAgeGroup
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
        assertEquals(4, result.size(), "The result should contain 4 age groups");

        assertTrue(result.containsKey("Children (0-12)"), "Group 'Children (0-12)' should be present");
        assertEquals(1.0, result.get("Children (0-12)"), "Piste usage for 'Children (0-12)' should be 1.0");

        assertTrue(result.containsKey("Teens (13-19)"), "Group 'Teens (13-19)' should be present");
        assertEquals(1.0, result.get("Teens (13-19)"), "Piste usage for 'Teens (13-19)' should be 1.0");

        assertTrue(result.containsKey("Adults (20-59)"), "Group 'Adults (20-59)' should be present");
        assertEquals(1.0, result.get("Adults (20-59)"), "Piste usage for 'Adults (20-59)' should be 1.0");

        assertTrue(result.containsKey("Seniors (60+)"), "Group 'Seniors (60+)' should be present");
        assertEquals(1.0, result.get("Seniors (60+)"), "Piste usage for 'Seniors (60+)' should be 1.0");

        System.out.println("testAnalyzePisteUsageByAgeGroup: Test succeeded!");
    }

    // Test for analyzeSkierEngagement
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

        System.out.println("testAnalyzeSkierEngagement: Test succeeded!");
    }

    // Test for findTopSpendingSkiers
    @Test
    void testFindTopSpendingSkiers() {
        // Création de deux skieurs avec des dépenses différentes
        Skier skier1 = new Skier(1L, "John", "Doe", LocalDate.now().minusYears(20), "CityA", null, new HashSet<>(), new HashSet<>());
        Skier skier2 = new Skier(2L, "Jane", "Doe", LocalDate.now().minusYears(22), "CityB", null, new HashSet<>(), new HashSet<>());

        // Création de cours avec différents prix
        Course course1 = new Course(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100f, 2, new HashSet<>());
        Course course2 = new Course(2L, 1, TypeCourse.INDIVIDUAL, Support.SNOWBOARD, 200f, 3, new HashSet<>());

        // Ajout d'inscriptions pour chaque skieur
        skier1.getRegistrations().add(new Registration(1L, 10, skier1, course1));
        skier1.getRegistrations().add(new Registration(2L, 11, skier1, course2));
        skier2.getRegistrations().add(new Registration(3L, 12, skier2, course1));

        // Simulation des appels aux méthodes du repository
        when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier1));
        when(skierRepository.findById(2L)).thenReturn(Optional.of(skier2));

        // Exécution de la méthode à tester
        List<Skier> result = skierServices.findTopSpendingSkiers(1);

        // Vérifications des résultats
        assertEquals(1, result.size(), "The result size should be 1 for top spender");
        assertEquals(skier1, result.get(0), "Top spending skier should be skier1");

        System.out.println("testFindTopSpendingSkiers: Test succeeded!");
    }


    // Test for getAverageAgeBySubscriptionType
    @Test
    void testGetAverageAgeBySubscriptionType() {
        LocalDate today = LocalDate.now();

        // Création de skieurs avec chaque type d'abonnement
        Subscription annualSubscription = new Subscription(1L, today.minusYears(1), today.plusYears(1), 300f, TypeSubscription.ANNUAL);
        Subscription monthlySubscription = new Subscription(2L, today.minusMonths(1), today.plusMonths(1), 50f, TypeSubscription.MONTHLY);
        Subscription semiAnnualSubscription = new Subscription(3L, today.minusMonths(6), today.plusMonths(6), 150f, TypeSubscription.SEMESTRIEL);

        Skier skierAnnual1 = new Skier(1L, "John", "Doe", today.minusYears(25), "CityA", annualSubscription, new HashSet<>(), new HashSet<>());
        Skier skierAnnual2 = new Skier(2L, "Alice", "Smith", today.minusYears(35), "CityB", annualSubscription, new HashSet<>(), new HashSet<>());
        Skier skierMonthly = new Skier(3L, "Bob", "Brown", today.minusYears(30), "CityC", monthlySubscription, new HashSet<>(), new HashSet<>());
        Skier skierSemiAnnual = new Skier(4L, "Carol", "White", today.minusYears(40), "CityD", semiAnnualSubscription, new HashSet<>(), new HashSet<>());

        // Simulation de la méthode findBySubscription_TypeSub pour chaque type d'abonnement
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(Arrays.asList(skierAnnual1, skierAnnual2));
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.MONTHLY)).thenReturn(Collections.singletonList(skierMonthly));
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.SEMESTRIEL)).thenReturn(Collections.singletonList(skierSemiAnnual));

        // Appel de la méthode à tester
        Map<TypeSubscription, Double> result = skierServices.getAverageAgeBySubscriptionType();

        // Calculs d'âge moyen attendus
        double expectedAnnualAge = (25 + 35) / 2.0;  // Moyenne des âges pour les abonnements ANNUAL
        double expectedMonthlyAge = 30.0;            // Un seul skieur avec abonnement MONTHLY
        double expectedSemiAnnualAge = 40.0;         // Un seul skieur avec abonnement SEMESTRIEL

        // Assertions pour vérifier les âges moyens calculés
        assertEquals(expectedAnnualAge, result.get(TypeSubscription.ANNUAL), "L'âge moyen pour l'abonnement ANNUAL doit être " + expectedAnnualAge);
        assertEquals(expectedMonthlyAge, result.get(TypeSubscription.MONTHLY), "L'âge moyen pour l'abonnement MONTHLY doit être " + expectedMonthlyAge);
        assertEquals(expectedSemiAnnualAge, result.get(TypeSubscription.SEMESTRIEL), "L'âge moyen pour l'abonnement SEMESTRIEL doit être " + expectedSemiAnnualAge);

        // Vérification de la taille pour s'assurer que seuls les types ajoutés sont inclus
        assertEquals(3, result.size(), "Le résultat doit contenir uniquement les 3 types d'abonnement testés (ANNUAL, MONTHLY, SEMESTRIEL)");

        System.out.println("testGetAverageAgeBySubscriptionType: Test succeeded!");
    }


    // Test for calculateTotalSpendingBySkier
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

        System.out.println("testCalculateTotalSpendingBySkier: Test succeeded!");
    }
}
