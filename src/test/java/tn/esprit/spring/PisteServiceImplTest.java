package tn.esprit.spring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.PisteServicesImpl;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PisteServiceImplTest {
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
        assertEquals("Piste A", actualPistes.get(0).getNamePiste());
        // Verifying the interaction with the mocked repository
        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    void addPiste() {
        // Mocking the repository save method
        when(pisteRepository.save(piste)).thenReturn(piste);
        // Calling the method under test
        Piste savedPiste = pisteService.addPiste(piste);
        // Asserting the results
        assertNotNull(savedPiste);
        assertEquals("Piste A", savedPiste.getNamePiste());
        // Verifying the interaction with the mocked repository
        verify(pisteRepository, times(1)).save(piste);
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

}


