package tn.esprit.spring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.PisteServicesImpl;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


   class PisteServiceImplTest {
    @Mock
    private IPisteRepository pisteRepository;
    @Mock
    private ISkierRepository skierRepository;
    @InjectMocks
    private PisteServicesImpl pisteService;

    private Skier skier;
    private Piste piste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Piste A");
        piste.setColor(Color.BLUE);
        piste.setLength(300);
        piste.setSlope(10);
        skier = new Skier();
        skier.setPistes(new HashSet<>()); // Initialize the pistes collection
        skier.setNumSkier(1L);
    }

    @Test
    void retrieveAllPistes() {
        List<Piste> expectedPistes = Arrays.asList(piste);
        // Mocking the repository response
        when(pisteRepository.findAll()).thenReturn(expectedPistes);
        // Calling the method under test
        List<Piste> actualPistes = pisteService.retrieveAllPistes();
        // Asserting the results
        assertNotNull(actualPistes);
        assertEquals(1, actualPistes.size());
        assertEquals(300, actualPistes.get(0).getLength());
        // Verifying the interaction with the mocked repository
        verify(pisteRepository, times(1)).findAll();
    }

       @Test
       void addPiste() {
           // Create a PisteDTO for testing
           PisteDTO pisteDTO = new PisteDTO("Piste A", "RED", 500, 30);

           // Mock the conversion and repository save behavior
           Piste piste = new Piste();
           piste.setNamePiste(pisteDTO.getNamePiste());
           piste.setColor(Color.valueOf(pisteDTO.getColor().toUpperCase()));
           piste.setLength(pisteDTO.getLength());
           piste.setSlope(pisteDTO.getSlope());

           // Mocking the repository save method
           when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

           // Calling the method under test
           Piste savedPiste = pisteService.addPiste(pisteDTO);

           // Asserting the results
           assertNotNull(savedPiste);
           assertEquals("Piste A", savedPiste.getNamePiste());
           assertEquals(Color.RED, savedPiste.getColor());
           assertEquals(500, savedPiste.getLength());
           assertEquals(30, savedPiste.getSlope());

           // Verifying the interaction with the mocked repository
           verify(pisteRepository, times(1)).save(any(Piste.class));
       }
    @Test
    void removePiste() {
        // Act
        pisteService.removePiste(1L);
        // Assert and Verify
        verify(pisteRepository, times(1)).deleteById(1L);
    }

    @Test
    void retrievePiste() {
        // Mocking the repository response
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        // Calling the method under test
        Piste retrievedPiste = pisteService.retrievePiste(1L);
        // Asserting the results
        assertNotNull(retrievedPiste);
        assertEquals("Piste A", retrievedPiste.getNamePiste());
        // Verifying the interaction with the mocked repository
        verify(pisteRepository, times(1)).findById(1L);
    }

    @Test
    void retrievePiste_NotFound() {
        // Mocking the repository response when the Piste is not found
        when(pisteRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> pisteService.retrievePiste(1L));
    }
    @Test
    void addPisteAndAssignToSkier_ShouldAddPisteAndAssignToSkier() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.save(piste)).thenReturn(piste);
        // Act
        Piste savedPiste = pisteService.addPisteAndAssignToSkier(piste, 1L);
        // Assert
        assertNotNull(savedPiste);
        assertEquals("Piste A", savedPiste.getNamePiste());
        verify(skierRepository, times(1)).findById(1L);
        verify(pisteRepository, times(1)).save(piste);
        verify(skierRepository, times(1)).save(skier);
        assertTrue(skier.getPistes().contains(piste), "The piste should be assigned to the skier");
    }


    @Test
    void addPisteAndAssignToSkiers_ShouldThrowExceptionWhenSkierNotFound() {
        // Given
        Piste piste = new Piste();
        piste.setNumPiste(1L);

        List<Long> skierIds = Arrays.asList(101L);

        // Mocking the repository behavior
        when(pisteRepository.save(piste)).thenReturn(piste);
        when(skierRepository.findById(101L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pisteService.addPisteAndAssignToSkiers(piste, skierIds);
        });

        assertEquals("Skier not found with ID: 101", exception.getMessage());

        // Verify that the repository was called
        verify(pisteRepository, times(1)).save(piste);
        verify(skierRepository, times(1)).findById(101L);
        verify(skierRepository, never()).save(any(Skier.class));
    }
    @Test
    void findPistesBySkierCity() {
        // Arrange
        List<Piste> expectedPistes = Arrays.asList(piste);
        when(pisteRepository.findBySkiersCity("Paris")).thenReturn(expectedPistes);
        // Act
        List<Piste> actualPistes = pisteService.findPistesBySkierCity("Paris");
        // Assert
        assertNotNull(actualPistes, "Pistes list should not be null");
        assertFalse(actualPistes.isEmpty(), "Pistes list should not be empty");
        assertEquals("Piste A", actualPistes.get(0).getNamePiste(), "Piste name should match");
        verify(pisteRepository, times(1)).findBySkiersCity("Paris");
    }

    @Test
    void findPistesByColor() {
        // Arrange
        List<Piste> expectedPistes = Arrays.asList(piste);
        when(pisteRepository.findByColor(Color.BLUE)).thenReturn(expectedPistes);
        // Act
        List<Piste> actualPistes = pisteService.findPistesByColor(Color.BLUE);
        // Assert
        assertNotNull(actualPistes, "Pistes list should not be null");
        assertFalse(actualPistes.isEmpty(), "Pistes list should not be empty");
        assertEquals(Color.BLUE, actualPistes.get(0).getColor(), "Piste color should match");
        verify(pisteRepository, times(1)).findByColor(Color.BLUE);
    }

       @Test
       void testFindPopularPistes_withSkierHavingEnoughCourses() {
           // Créer des objets Piste
           Piste piste1 = new Piste();
           Piste piste2 = new Piste();

           // Créer des skieurs
           Skier skier1 = new Skier();
           skier1.setRegistrations(new HashSet<>(Arrays.asList(new Registration(), new Registration()))); // 2 cours
           skier1.setPistes(new HashSet<>(Arrays.asList(piste1)));

           Skier skier2 = new Skier();
           skier2.setRegistrations(new HashSet<>(Arrays.asList(new Registration(), new Registration(), new Registration()))); // 3 cours
           skier2.setPistes(new HashSet<>(Arrays.asList(piste2)));

           // Simuler le retour du repository
           when(skierRepository.findAll()).thenReturn(Arrays.asList(skier1, skier2));

           // Appeler la méthode à tester
           List<Piste> result = pisteService.findPopularPistes(2);

           // Vérifier les résultats
           assertEquals(2, result.size());
           assertEquals(new HashSet<>(Arrays.asList(piste1, piste2)), new HashSet<>(result));
       }

       @Test
       void testFindPistesForSkierByAge_under18() {
           // Créer un skieur de moins de 18 ans
           Skier youngSkier = new Skier();
           youngSkier.setNumSkier(1L);
           youngSkier.setDateOfBirth(LocalDate.now().minusYears(15)); // 15 ans

           // Créer des pistes avec des couleurs différentes
           Piste greenPiste = new Piste("Green Piste", Color.GREEN);
           Piste bluePiste = new Piste("Blue Piste", Color.BLUE);
           Piste redPiste = new Piste("Red Piste", Color.RED);

           // Simuler le retour du repository pour le skieur et les pistes
           when(skierRepository.findById(1L)).thenReturn(Optional.of(youngSkier));
           when(pisteRepository.findAll()).thenReturn(Arrays.asList(greenPiste, bluePiste, redPiste));

           // Appeler la méthode à tester
           List<Piste> result = pisteService.findPistesForSkierByAge(1L);

           // Vérifier que seules les pistes vertes et bleues sont renvoyées pour un skieur de moins de 18 ans
           assertEquals(2, result.size());
           assertEquals(Arrays.asList(greenPiste, bluePiste), result);
       }

       @Test
       void testFindPistesForSkierByAge_between18And50() {
           // Créer un skieur entre 18 et 50 ans
           Skier adultSkier = new Skier();
           adultSkier.setNumSkier(2L);
           adultSkier.setDateOfBirth(LocalDate.now().minusYears(30)); // 30 ans

           // Créer des pistes avec des couleurs différentes
           Piste greenPiste = new Piste("Green Piste", Color.GREEN);
           Piste bluePiste = new Piste("Blue Piste", Color.BLUE);
           Piste redPiste = new Piste("Red Piste", Color.RED);
           Piste blackPiste = new Piste("Black Piste", Color.BLACK);

           // Simuler le retour du repository pour le skieur et les pistes
           when(skierRepository.findById(2L)).thenReturn(Optional.of(adultSkier));
           when(pisteRepository.findAll()).thenReturn(Arrays.asList(greenPiste, bluePiste, redPiste, blackPiste));

           // Appeler la méthode à tester
           List<Piste> result = pisteService.findPistesForSkierByAge(2L);

           // Vérifier que toutes les pistes sauf la verte sont renvoyées pour un skieur adulte
           assertEquals(3, result.size());
           assertEquals(Arrays.asList(bluePiste, redPiste, blackPiste), result);
       }

       @Test
       void testFindPistesForSkierByAge_over50() {
           // Créer un skieur de plus de 50 ans
           Skier seniorSkier = new Skier();
           seniorSkier.setNumSkier(3L);
           seniorSkier.setDateOfBirth(LocalDate.now().minusYears(60)); // 60 ans

           // Créer des pistes avec des couleurs différentes
           Piste greenPiste = new Piste("Blue Piste",Color.GREEN);
           Piste bluePiste = new Piste("Blue Piste", Color.BLUE);
           Piste redPiste = new Piste("Red Piste", Color.RED);

           // Simuler le retour du repository pour le skieur et les pistes
           when(skierRepository.findById(3L)).thenReturn(Optional.of(seniorSkier));
           when(pisteRepository.findAll()).thenReturn(Arrays.asList(greenPiste, bluePiste, redPiste));

           // Appeler la méthode à tester
           List<Piste> result = pisteService.findPistesForSkierByAge(3L);

           // Vérifier que seules les pistes vertes et bleues sont renvoyées pour un skieur de plus de 50 ans
           assertEquals(2, result.size());
           assertEquals(Arrays.asList(greenPiste, bluePiste), result);
       }


   }


