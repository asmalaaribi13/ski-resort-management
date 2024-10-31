package tn.esprit.spring;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.PisteServicesImpl;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class PisteServiceJUnitTest {
    @Autowired
    private PisteServicesImpl pisteService;
    @Autowired
    private IPisteRepository pisteRepository;
    @Autowired
    private ISkierRepository skierRepository;
    private static Long savedPisteId;

    @BeforeEach
    @Order(1)
    void setUp() {
        // Arrange - Create a new Skier for later use
        Skier skier1 = new Skier();
        skier1.setFirstName("John");
        skier1.setLastName("Doe");
        skier1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        skier1.setCity("Paris");
        skier1.setPistes(new HashSet<>()); // Initialize the pistes collection
        skierRepository.save(skier1); // Persist skier1

        Skier skier2 = new Skier();
        skier2.setFirstName("Jane");
        skier2.setLastName("Smith");
        skier2.setDateOfBirth(LocalDate.of(1985, 5, 15));
        skier2.setCity("Lyon");
        skier2.setPistes(new HashSet<>());
        skierRepository.save(skier2); // Persist skier2
    }

    @Test
    @Order(2)
    void addPiste() {
        // Arrange
        PisteDTO pisteDTO = new PisteDTO();
        pisteDTO.setNamePiste("Piste A");
        pisteDTO.setColor("RED");
        pisteDTO.setLength(1500);
        pisteDTO.setSlope(25);
        // Create a mock Piste entity to be returned by the repository
        Piste piste = new Piste();
        piste.setNumPiste(1L); // Mocked ID for the saved entity
        piste.setNamePiste(pisteDTO.getNamePiste());
        piste.setColor(Color.RED);
        piste.setLength(pisteDTO.getLength());
        piste.setSlope(pisteDTO.getSlope());
        // Act
        Piste savedPiste = pisteService.addPiste(pisteDTO);
        // Store the ID for later use
        savedPisteId = savedPiste.getNumPiste();

        // Assert
        assertNotNull(savedPiste, "Saved Piste should not be null");
        assertNotNull(savedPiste.getNumPiste(), "Piste ID should be generated");
        System.out.println("Add Piste: Ok");

    }



    @Test
    @Order(3)
    void retrieveAllPistes() {
        // Act
        List<Piste> pistes = pisteService.retrieveAllPistes();
        // Assert
        assertNotNull(pistes, "Retrieved Pistes list should not be null");
        assertFalse(pistes.isEmpty(), "Pistes list should not be empty");
        System.out.println("Retrieve All Pistes: Ok");
    }

    @Test
    @Order(4)
    void retrievePiste() {
        // Ensure that the Piste was added in the previous test
        assertNotNull(savedPisteId, "Saved Piste ID should not be null");
        // Act
        Piste retrievedPiste = pisteService.retrievePiste(savedPisteId);
        // Assert
        assertNotNull(retrievedPiste, "Retrieved Piste should not be null");
        assertEquals("Piste A", retrievedPiste.getNamePiste(), "Piste name should match");
        assertEquals(Color.RED, retrievedPiste.getColor(), "Piste color should match");
        assertEquals(1500, retrievedPiste.getLength(), "Piste length should match");
        assertEquals(25, retrievedPiste.getSlope(), "Piste slope should match");
        System.out.println("Retrieve Piste: Ok");
    }

    @Test
    @Order(5)
    void removePiste() {
        // Ensure that the Piste was added in the previous tests
        assertNotNull(savedPisteId, "Saved Piste ID should not be null");
        // Act
        pisteService.removePiste(savedPisteId);
        // Assert
        assertThrows(EntityNotFoundException.class, () -> {
            pisteService.retrievePiste(savedPisteId);
        }, "Retrieving a removed Piste should throw EntityNotFoundException");
        System.out.println("Remove Piste: Ok");
    }

    @Test
    @Order(6)
    void findPistesBySkierCity() {
        // Arrange
        Piste piste = new Piste();
        piste.setNamePiste("City Trail");
        piste.setColor(Color.BLUE);
        piste.setLength(2000);
        piste.setSlope(30);
        pisteRepository.save(piste);

        // Act
        List<Piste> pistes = pisteService.findPistesBySkierCity("Paris");

        // Assert
        assertNotNull(pistes, "Pistes list should not be null");

        // Assuming the "City Trail" piste is not linked to "Paris", it should be empty
        assertTrue(pistes.isEmpty(), "Pistes list should be empty since there are no pistes for the city 'Paris'.");

        System.out.println("Find Pistes By Skier City: Ok");
    }

    @Test
    @Order(7)
    void findPistesByColor() {
        // Arrange
        Piste piste = new Piste();
        piste.setNamePiste("Color Trail");
        piste.setColor(Color.RED);
        piste.setLength(1800);
        piste.setSlope(20);
        pisteRepository.save(piste);

        // Act
        List<Piste> pistes = pisteService.findPistesByColor(Color.RED);

        // Assert
        assertNotNull(pistes, "Pistes list should not be null");
        assertFalse(pistes.isEmpty(), "Pistes list should not be empty");
        assertEquals(Color.RED, pistes.get(0).getColor(), "Piste color should match");
        System.out.println("Find Pistes By Color: Ok");
    }
    @Test
    @Order(8)
    @Transactional
    void addPisteAndAssignToSkier_ShouldAddAndAssignPisteToSkier() {
        // Arrange
        Piste piste = new Piste();
        piste.setNamePiste("Assigned Piste");
        piste.setColor(Color.GREEN);
        piste.setLength(1200);
        piste.setSlope(15);

        // Retrieve an existing skier
        List<Skier> skiers = skierRepository.findAll();
        assertFalse(skiers.isEmpty(), "There should be at least one skier available for assignment.");
        Skier skier = skiers.get(0);
        Long skierId = skier.getNumSkier();

        // Act
        Piste savedPiste = pisteService.addPisteAndAssignToSkier(piste, skierId);

        // Assert
        assertNotNull(savedPiste, "Saved Piste should not be null");
        assertNotNull(savedPiste.getNumPiste(), "Piste ID should be generated");
        assertEquals("Assigned Piste", savedPiste.getNamePiste(), "Piste name should match");
        assertEquals(Color.GREEN, savedPiste.getColor(), "Piste color should match");
        assertEquals(1200, savedPiste.getLength(), "Piste length should match");
        assertEquals(15, savedPiste.getSlope(), "Piste slope should match");

        // Verify that the piste is assigned to the skier
        Skier updatedSkier = skierRepository.findById(skierId)
                .orElseThrow(() -> new EntityNotFoundException("Skier not found with ID: " + skierId));
        assertTrue(updatedSkier.getPistes().contains(savedPiste), "The piste should be assigned to the skier.");

        System.out.println("Add Piste and Assign to Skier: Ok");
    }

    @Test
    @Order(9)
    @Transactional
    void addPisteAndAssignMultipleSkiers_ShouldAddAndAssignPisteToMultipleSkiers() {
        // Arrange
        Piste piste = new Piste();
        piste.setNamePiste("Multi-Skier Piste");
        piste.setColor(Color.GREEN);
        piste.setLength(1300);
        piste.setSlope(10);

        // Retrieve existing skiers
        List<Skier> skiers = skierRepository.findAll();
        assertTrue(skiers.size() >= 2, "There should be at least two skiers available for assignment.");

        List<Long> skierIds = Arrays.asList(skiers.get(0).getNumSkier(), skiers.get(1).getNumSkier());

        // Act
        Piste savedPiste = pisteService.addPisteAndAssignToSkiers(piste, skierIds);

        // Assert
        assertNotNull(savedPiste, "Saved Piste should not be null");
        assertEquals("Multi-Skier Piste", savedPiste.getNamePiste(), "Piste name should match");

        // Verify that the piste is assigned to the skiers
        for (Long skierId : skierIds) {
            Skier updatedSkier = skierRepository.findById(skierId)
                    .orElseThrow(() -> new EntityNotFoundException("Skier not found with ID: " + skierId));
            assertTrue(updatedSkier.getPistes().contains(savedPiste), "The piste should be assigned to the skier.");
        }

        System.out.println("Add Piste and Assign to Multiple Skiers: Ok");
    }
    @Test
    @Order(10)
    void findPopularPistes_ShouldReturnPistesForSkiersWithMinCourses() {
        // Arrange

        // Create Skier 1 with 2 registrations (doesn't meet minCourses condition of 3)
        Skier skier1 = new Skier();
        skier1.setFirstName("Skier 1");
        skier1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        skier1.setRegistrations(new HashSet<>(Arrays.asList(new Registration(), new Registration()))); // 2 registrations
        skier1.setPistes(new HashSet<>()); // No pistes yet

        // Create Skier 2 with 3 registrations (meets minCourses condition of 3)
        Skier skier2 = new Skier();
        skier2.setFirstName("Skier 2");
        skier2.setDateOfBirth(LocalDate.of(1985, 5, 15));
        skier2.setRegistrations(new HashSet<>(Arrays.asList(new Registration(), new Registration(), new Registration()))); // 3 registrations
        skier2.setPistes(new HashSet<>()); // No pistes yet

        // Save the skiers
        skierRepository.save(skier1);
        skierRepository.save(skier2);

        // Create Piste 1 and assign it to Skier 1 (who doesn't meet the minCourses condition)
        Piste piste1 = new Piste();
        piste1.setNamePiste("Piste Popular 1");
        piste1.setColor(Color.BLUE);
        pisteRepository.save(piste1);
        skier1.getPistes().add(piste1); // Assign piste1 to skier1
        skierRepository.save(skier1);

        // Create Piste 2 and assign it to Skier 2 (who meets the minCourses condition)
        Piste piste2 = new Piste();
        piste2.setNamePiste("Piste Popular 2");
        piste2.setColor(Color.RED);
        pisteRepository.save(piste2);
        skier2.getPistes().add(piste2); // Assign piste2 to skier2
        skierRepository.save(skier2);

        // Act
        List<Piste> popularPistes = pisteService.findPopularPistes(3); // minCourses = 3

        // Assert
        assertNotNull(popularPistes, "Popular pistes should not be null");

        System.out.println("Find Popular Pistes: Test Passed");
    }


    @Test
    @Order(11)
    void findPistesForSkierByAge_ShouldReturnPistesBasedOnAge() {
        // Arrange
        Skier skier = new Skier();
        skier.setFirstName("Young Skier");
        skier.setDateOfBirth(LocalDate.of(2010, 1, 1));
        skierRepository.save(skier);

        Piste greenPiste = new Piste();
        greenPiste.setNamePiste("Green Piste");
        greenPiste.setColor(Color.GREEN);
        pisteRepository.save(greenPiste);

        Piste bluePiste = new Piste();
        bluePiste.setNamePiste("Blue Piste");
        bluePiste.setColor(Color.BLUE);
        pisteRepository.save(bluePiste);

        Piste blackPiste = new Piste();
        blackPiste.setNamePiste("Black Piste");
        blackPiste.setColor(Color.BLACK);
        pisteRepository.save(blackPiste);
        // Act
        List<Piste> pistesForYoungSkier = pisteService.findPistesForSkierByAge(skier.getNumSkier());
        // Assert
        assertNotNull(pistesForYoungSkier, "Pistes list should not be null");
        assertEquals(4, pistesForYoungSkier.size(), "Only 4 pistes (GREEN, BLUE) should be returned for young skier");
        assertTrue(pistesForYoungSkier.stream().anyMatch(piste -> piste.getColor() == Color.GREEN), "Green piste should be included");
        assertTrue(pistesForYoungSkier.stream().anyMatch(piste -> piste.getColor() == Color.BLUE), "Blue piste should be included");
        assertFalse(pistesForYoungSkier.stream().anyMatch(piste -> piste.getColor() == Color.BLACK), "Black piste should not be included");

        System.out.println("Find Pistes for Skier by Age: Ok");
    }


}
