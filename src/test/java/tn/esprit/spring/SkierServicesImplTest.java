package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays; // Make sure to import Arrays


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
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    private Skier skier;
    private Subscription currentSubscription;
    private Subscription newSubscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("Mock setup completed.");

        // Initialize skier and subscriptions
        skier = new Skier();

        // Initialize the current subscription
        currentSubscription = new Subscription();
        currentSubscription.setTypeSub(TypeSubscription.MONTHLY);
        currentSubscription.setStartDate(LocalDate.now());
        currentSubscription.setEndDate(LocalDate.now().plusMonths(1));

        // Initialize the new subscription
        newSubscription = new Subscription();
        newSubscription.setTypeSub(TypeSubscription.ANNUAL);
        newSubscription.setStartDate(LocalDate.now());
        newSubscription.setEndDate(LocalDate.now().plusYears(1));

        skier.setSubscription(currentSubscription);
        skier.setRegistrations(new HashSet<>());
    }

    @Test
    void retrieveAllSkiers_ShouldReturnListOfSkiers() {
        System.out.println("Starting test: retrieveAllSkiers_ShouldReturnListOfSkiers");

        // Arrange
        List<Skier> expectedSkiers = new ArrayList<>();
        Skier sampleSkier = new Skier();
        expectedSkiers.add(sampleSkier);
        when(skierRepository.findAll()).thenReturn(expectedSkiers);

        // Act
        List<Skier> actualSkiers = skierServices.retrieveAllSkiers();

        // Assert
        assertEquals(expectedSkiers, actualSkiers);
        verify(skierRepository).findAll();

        System.out.println("Expected Skiers: " + expectedSkiers);
        System.out.println("Actual Skiers: " + actualSkiers);
        System.out.println("Test passed: retrieveAllSkiers_ShouldReturnListOfSkiers\n");
    }

    @Test
    void addSkier_ShouldSaveSkierAndSetEndDate() {
        System.out.println("Starting test: addSkier_ShouldSaveSkierAndSetEndDate");

        // Arrange
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(subscription.getStartDate().plusYears(1));
        skier.setSubscription(subscription);

        when(skierRepository.save(skier)).thenReturn(skier);

        // Act
        Skier savedSkier = skierServices.addSkier(skier);

        // Assert
        assertNotNull(savedSkier);
        assertEquals(LocalDate.now().plusYears(1), savedSkier.getSubscription().getEndDate());
        verify(skierRepository).save(skier);

        System.out.println("Saved Skier: " + savedSkier);
        System.out.println("Test passed: addSkier_ShouldSaveSkierAndSetEndDate\n");
    }

    @Test
    void assignSkierToSubscription_ShouldAssignSubscriptionToSkier() {
        System.out.println("Starting test: assignSkierToSubscription_ShouldAssignSubscriptionToSkier");

        // Arrange
        Long skierId = 1L;
        Long subscriptionId = 2L;
        Subscription subscription = new Subscription();

        when(skierRepository.findById(skierId)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(skier)).thenReturn(skier);

        // Act
        Skier updatedSkier = skierServices.assignSkierToSubscription(skierId, subscriptionId);

        // Assert
        assertEquals(subscription, updatedSkier.getSubscription());
        verify(skierRepository).save(skier);

        System.out.println("Assigned Skier: " + updatedSkier);
        System.out.println("Test passed: assignSkierToSubscription_ShouldAssignSubscriptionToSkier\n");
    }

    @Test
    void retrieveSkier_ShouldReturnSkier() {
        System.out.println("Starting test: retrieveSkier_ShouldReturnSkier");

        // Arrange
        Long skierId = 1L;
        Skier expectedSkier = new Skier();
        when(skierRepository.findById(skierId)).thenReturn(Optional.of(expectedSkier));

        // Act
        Skier actualSkier = skierServices.retrieveSkier(skierId);

        // Assert
        assertEquals(expectedSkier, actualSkier);
        verify(skierRepository).findById(skierId);

        System.out.println("Expected Skier: " + expectedSkier);
        System.out.println("Actual Skier: " + actualSkier);
        System.out.println("Test passed: retrieveSkier_ShouldReturnSkier\n");
    }

    @Test
    void removeSkier_ShouldDeleteSkier() {
        System.out.println("Starting test: removeSkier_ShouldDeleteSkier");

        // Arrange
        Long skierId = 1L;

        // Act
        skierServices.removeSkier(skierId);

        // Assert
        verify(skierRepository).deleteById(skierId);

        System.out.println("Skier with ID " + skierId + " successfully deleted.");
        System.out.println("Test passed: removeSkier_ShouldDeleteSkier\n");
    }

    // Utility method to create a Set of registrations
    private Set<Registration> createRegistrations(int count) {
        Set<Registration> registrations = new HashSet<>();
        for (int i = 0; i < count; i++) {
            registrations.add(new Registration());
        }
        return registrations;
    }



    @Test
    void findSkiersWithMultipleSupports_ShouldReturnSkiersWithMoreThanOneSupport() {
        System.out.println("Starting test: findSkiersWithMultipleSupports_ShouldReturnSkiersWithMoreThanOneSupport");

        // Arrange
        Skier skier1 = new Skier(); // Skier with multiple supports
        Skier skier2 = new Skier(); // Skier with one support only

        // Create courses with different supports
        Course course1 = new Course();
        course1.setSupport(Support.SKI); // SKI support
        Course course2 = new Course();
        course2.setSupport(Support.SNOWBOARD); // SNOWBOARD support

        Course course3 = new Course();
        course3.setSupport(Support.SKI); // SKI support for skier2 (only one support type)

        // Create registrations for skier1 (multiple supports)
        Registration registration1 = new Registration();
        registration1.setCourse(course1); // SKI course
        Registration registration2 = new Registration();
        registration2.setCourse(course2); // SNOWBOARD course

        Set<Registration> skier1Registrations = new HashSet<>();
        skier1Registrations.add(registration1);
        skier1Registrations.add(registration2);
        skier1.setRegistrations(skier1Registrations);

        // Create registrations for skier2 (only one support type)
        Registration registration3 = new Registration();
        registration3.setCourse(course3);

        Set<Registration> skier2Registrations = new HashSet<>();
        skier2Registrations.add(registration3);
        skier2.setRegistrations(skier2Registrations);

        List<Skier> skiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findAll()).thenReturn(skiers);

        // Act
        List<Skier> result = skierServices.findSkiersWithMultipleSupports();

        // Assert
        assertEquals(1, result.size()); // Only skier1 has multiple supports
        assertTrue(result.contains(skier1));
        assertFalse(result.contains(skier2)); // skier2 should be excluded

        System.out.println("Test passed: findSkiersWithMultipleSupports_ShouldReturnSkiersWithMoreThanOneSupport\n");
    }

    @Test
    void analyzeSkierEngagement_ShouldReturnEngagementStatistics() {
        System.out.println("Starting test: analyzeSkierEngagement_ShouldReturnEngagementStatistics");

        // Arrange
        Skier skier1 = new Skier();
        skier1.setRegistrations(createRegistrations(2));
        Skier skier2 = new Skier();
        skier2.setRegistrations(createRegistrations(3));
        Skier skier3 = new Skier();
        skier3.setRegistrations(createRegistrations(1));
        List<Skier> skiers = Arrays.asList(skier1, skier2, skier3);
        when(skierRepository.findAll()).thenReturn(skiers);

        // Act
        Map<String, Object> statistics = skierServices.analyzeSkierEngagement();

        // Assert
        assertNotNull(statistics);
        assertEquals(2.0, statistics.get("averageCoursesPerSkier"));
        assertEquals(skier2, statistics.get("mostActiveSkier"));

        System.out.println("Engagement Statistics: " + statistics);
        System.out.println("Test passed: analyzeSkierEngagement_ShouldReturnEngagementStatistics\n");
    }

    @Test
    void testFindSkiersByPisteColor() {
        // Given
        Color targetColor = Color.GREEN;

        // Create pistes
        Piste greenPiste = new Piste();
        greenPiste.setColor(Color.GREEN);

        Piste bluePiste = new Piste();
        bluePiste.setColor(Color.BLUE);

        Piste redPiste = new Piste();
        redPiste.setColor(Color.RED);

        // Create skiers
        Skier skier1 = new Skier();
        skier1.setPistes(new HashSet<>(Arrays.asList(greenPiste, bluePiste)));

        Skier skier2 = new Skier();
        skier2.setPistes(new HashSet<>(Collections.singletonList(redPiste)));

        List<Skier> skiers = Arrays.asList(skier1, skier2);

        when(skierRepository.findAll()).thenReturn(skiers);

        // When
        List<Skier> result = skierServices.findSkiersByPisteColor(targetColor);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(skier1));
        assertFalse(result.contains(skier2));

        System.out.println("Test passed: testFindSkiersByPisteColor\n");
    }

    @Test
    void testCalculateTotalSpendingBySkier() {
        // Given
        Long numSkier = 1L;

        // Create a mock course with prices
        Course course1 = new Course();
        course1.setPrice(100f); // Set price for course1

        Course course2 = new Course();
        course2.setPrice(150f); // Set price for course2

        // Create registrations for the skier
        Registration registration1 = new Registration();
        registration1.setCourse(course1);

        Registration registration2 = new Registration();
        registration2.setCourse(course2);

        // Create a skier with registrations and a subscription
        Skier testSkier = new Skier(); // Renamed to avoid hiding the field
        testSkier.setRegistrations(new HashSet<>(Arrays.asList(registration1, registration2)));

        // Create a subscription with a price
        Subscription subscription = new Subscription();
        subscription.setPrice(50f); // Set price for subscription
        testSkier.setSubscription(subscription);

        // Mock the repository behavior
        when(skierRepository.findById(numSkier)).thenReturn(Optional.of(testSkier));

        // When
        Float totalSpending = skierServices.calculateTotalSpendingBySkier(numSkier);

        // Then
        Float expectedSpending = 100f + 150f + 50f; // 100 (course1) + 150 (course2) + 50 (subscription)
        assertEquals(expectedSpending, totalSpending);

        System.out.println("Test passed: testCalculateTotalSpendingBySkier\n");
    }


    @Test
    void testFindSkiersWithHighestAverageCoursePrice() {
        // Given
        int topN = 2;

        // Create Courses with different prices
        Course course1 = new Course();
        course1.setPrice(100f); // Price for course1

        Course course2 = new Course();
        course2.setPrice(150f); // Price for course2

        Course course3 = new Course();
        course3.setPrice(200f); // Price for course3

        // Create Registrations for skiers
        Registration registration1 = new Registration();
        registration1.setCourse(course1);

        Registration registration2 = new Registration();
        registration2.setCourse(course2);

        Registration registration3 = new Registration();
        registration3.setCourse(course3);

        // Create skiers with different registrations
        Skier skier1 = new Skier();
        skier1.setRegistrations(new HashSet<>(Arrays.asList(registration1, registration2))); // Average: (100 + 150) / 2 = 125

        Skier skier2 = new Skier();
        skier2.setRegistrations(new HashSet<>(Collections.singletonList(registration3))); // Average: 200

        Skier skier3 = new Skier();
        skier3.setRegistrations(new HashSet<>(Arrays.asList(registration1, registration3))); // Average: (100 + 200) / 2 = 150

        List<Skier> skiers = Arrays.asList(skier1, skier2, skier3);
        when(skierRepository.findAll()).thenReturn(skiers);

        // When
        List<Skier> result = skierServices.findSkiersWithHighestAverageCoursePrice(topN);

        // Then
        assertEquals(topN, result.size());
        assertEquals(skier2, result.get(0)); // skier2 has the highest average price (200)
        assertEquals(skier3, result.get(1)); // skier3 has the second highest average price (150)

        System.out.println("Test passed: testFindSkiersWithHighestAverageCoursePrice\n");
    }

    @Test
    void analyzePisteUsageByAgeGroup_ShouldReturnAverageUsageByAgeGroup() {
        // Given
        LocalDate currentDate = LocalDate.now();

        // Create mock skiers
        Skier skier1 = new Skier();
        skier1.setDateOfBirth(currentDate.minusYears(10)); // Age 10
        skier1.setPistes(new HashSet<>(Arrays.asList(new Piste(), new Piste()))); // 2 pistes

        Skier skier2 = new Skier();
        skier2.setDateOfBirth(currentDate.minusYears(15)); // Age 15
        skier2.setPistes(new HashSet<>(Arrays.asList(new Piste()))); // 1 piste

        Skier skier3 = new Skier();
        skier3.setDateOfBirth(currentDate.minusYears(30)); // Age 30
        skier3.setPistes(new HashSet<>(Arrays.asList(new Piste(), new Piste(), new Piste()))); // 3 pistes

        // Mock repository behavior
        List<Skier> skiers = Arrays.asList(skier1, skier2, skier3);
        when(skierRepository.findAll()).thenReturn(skiers);

        // When
        Map<String, Double> result = skierServices.analyzePisteUsageByAgeGroup();

        // Then
        assertNotNull(result);
        assertEquals(2.0, result.get("Children (0-12)")); // Expect 2 pistes from skier1
        assertEquals(1.0, result.get("Teens (13-19)")); // Expect 1 piste from skier2
        assertEquals(3.0, result.get("Adults (20-59)")); // Expect 3 pistes from skier3
        assertEquals(0.0, result.get("Seniors (60+)")); // No seniors in this test

        System.out.println("Average Piste Usage by Age Group: " + result);
        System.out.println("Test passed: analyzePisteUsageByAgeGroup_ShouldReturnAverageUsageByAgeGroup\n");
    }



























}
