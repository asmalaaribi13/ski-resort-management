package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    void calculateTotalCourseDurationForSkier_ShouldReturnTotalDuration() {
        System.out.println("Starting test: calculateTotalCourseDurationForSkier_ShouldReturnTotalDuration");

        // Arrange
        Long skierId = 1L;

        // Create courses
        Course course1 = new Course();
        course1.setTimeSlot(2); // Set time slot for course 1
        Course course2 = new Course();
        course2.setTimeSlot(3); // Set time slot for course 2

        // Create registrations using the default constructor
        Registration registration1 = new Registration();
        registration1.setCourse(course1);  // Use the setter to set the course
        Registration registration2 = new Registration();
        registration2.setCourse(course2);  // Use the setter to set the course

        // Add registrations to the skier
        Set<Registration> registrations = new HashSet<>();
        registrations.add(registration1);
        registrations.add(registration2);

        skier.setRegistrations(registrations);  // Set the registrations for the skier

        // Mock repository behavior
        when(skierRepository.findById(skierId)).thenReturn(Optional.of(skier));

        // Act
        int totalDuration = skierServices.calculateTotalCourseDurationForSkier(skierId);

        // Assert
        assertEquals(5, totalDuration);  // Expect the total duration to be 5

        // Console output for verification
        System.out.println("Total Duration: " + totalDuration);
        System.out.println("Test passed: calculateTotalCourseDurationForSkier_ShouldReturnTotalDuration\n");
    }

    @Test
    @DisplayName("Find the most active skier")
    void findMostActiveSkier_ShouldReturnMostActiveSkier() {
        System.out.println("Starting test: findMostActiveSkier_ShouldReturnMostActiveSkier");

        // Arrange
        Skier skier1 = new Skier();
        skier1.setRegistrations(createRegistrations(2));
        Skier skier2 = new Skier();
        skier2.setRegistrations(createRegistrations(3));
        List<Skier> skiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findAll()).thenReturn(skiers);

        // Act
        Skier mostActiveSkier = skierServices.findMostActiveSkier();

        // Assert
        assertEquals(skier2, mostActiveSkier);

        System.out.println("Most Active Skier: " + mostActiveSkier);
        System.out.println("Test passed: findMostActiveSkier_ShouldReturnMostActiveSkier\n");
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
    void findSkiersByAgeRange_ShouldReturnSkiersWithinAgeRange() {
        System.out.println("Starting test: findSkiersByAgeRange_ShouldReturnSkiersWithinAgeRange");

        // Arrange
        Skier skier1 = new Skier();
        skier1.setDateOfBirth(LocalDate.of(1990, 1, 1)); // Age 34 (if current year is 2024)

        Skier skier2 = new Skier();
        skier2.setDateOfBirth(LocalDate.of(2005, 1, 1)); // Age 19

        Skier skier3 = new Skier();
        skier3.setDateOfBirth(LocalDate.of(2010, 1, 1)); // Age 14

        List<Skier> skiers = Arrays.asList(skier1, skier2, skier3);
        when(skierRepository.findAll()).thenReturn(skiers);

        // Act
        List<Skier> result = skierServices.findSkiersByAgeRange(18, 35);

        // Assert
        assertEquals(2, result.size()); // Should return skier1 and skier2 (ages 34 and 19)
        assertTrue(result.contains(skier1));
        assertTrue(result.contains(skier2));
        assertFalse(result.contains(skier3)); // skier3 should be excluded (age 14)

        System.out.println("Test passed: findSkiersByAgeRange_ShouldReturnSkiersWithinAgeRange\n");
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












}
