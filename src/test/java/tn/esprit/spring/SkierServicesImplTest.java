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
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("Mock setup completed.");
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
        Skier skier = new Skier();
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
        Skier skier = new Skier();
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
        Skier skier = new Skier();

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
    void calculateTotalSkiersWithAnnualSubscription_ShouldReturnCount() {
        System.out.println("Starting test: calculateTotalSkiersWithAnnualSubscription_ShouldReturnCount");

        // Arrange
        Skier skier1 = new Skier();
        Subscription subscription1 = new Subscription();
        subscription1.setTypeSub(TypeSubscription.ANNUAL);
        skier1.setSubscription(subscription1);

        Skier skier2 = new Skier();
        Subscription subscription2 = new Subscription();
        subscription2.setTypeSub(TypeSubscription.MONTHLY);
        skier2.setSubscription(subscription2);

        List<Skier> skiers = Arrays.asList(skier1, skier2);
        when(skierRepository.findAll()).thenReturn(skiers);

        // Act
        long count = skierServices.calculateTotalSkiersWithAnnualSubscription();

        // Assert
        assertEquals(1, count);

        System.out.println("Total Skiers with Annual Subscription: " + count);
        System.out.println("Test passed: calculateTotalSkiersWithAnnualSubscription_ShouldReturnCount\n");
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
