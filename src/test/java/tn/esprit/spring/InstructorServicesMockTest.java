package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;
import tn.esprit.spring.services.InstructorServicesImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class InstructorServicesMockTest {

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldAddInstructorSuccessfully() {
        // Arrange
        Instructor instructor = createSampleInstructor("John", "Doe", LocalDate.of(2020, 1, 15));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor savedInstructor = instructorServices.addInstructor(instructor);

        // Assert
        assertNotNull(savedInstructor, "Saved instructor should not be null");
        assertEquals("John", savedInstructor.getFirstName());
        assertEquals("Doe", savedInstructor.getLastName());
        assertEquals(LocalDate.of(2020, 1, 15), savedInstructor.getDateOfHire());

        // Capture and verify saved instructor details
        ArgumentCaptor<Instructor> instructorCaptor = ArgumentCaptor.forClass(Instructor.class);
        verify(instructorRepository).save(instructorCaptor.capture());
        Instructor capturedInstructor = instructorCaptor.getValue();
        assertEquals("John", capturedInstructor.getFirstName());
        assertEquals("Doe", capturedInstructor.getLastName());
    }

    @Test
    public void shouldRetrieveAllInstructors() {
        // Arrange
        Instructor instructor = createSampleInstructor("Alice", "Smith", LocalDate.of(2021, 3, 5));
        when(instructorRepository.findAll()).thenReturn(List.of(instructor));

        // Act
        List<Instructor> instructors = instructorServices.retrieveAllInstructors();

        // Assert
        assertNotNull(instructors, "Instructors list should not be null");
        assertEquals(1, instructors.size(), "Instructors list size should be 1");
        assertEquals("Alice", instructors.get(0).getFirstName());

        // Verify repository interaction
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    public void shouldUpdateInstructorSuccessfully() {
        // Arrange
        Instructor instructor = createSampleInstructor("Jane", "Doe", LocalDate.of(2019, 6, 10));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        instructor.setFirstName("Jane Updated");
        instructor.setLastName("Doe Updated");
        Instructor updatedInstructor = instructorServices.updateInstructor(instructor);

        // Assert
        assertEquals("Jane Updated", updatedInstructor.getFirstName());
        assertEquals("Doe Updated", updatedInstructor.getLastName());

        // Capture and verify updated instructor details
        ArgumentCaptor<Instructor> instructorCaptor = ArgumentCaptor.forClass(Instructor.class);
        verify(instructorRepository).save(instructorCaptor.capture());
        Instructor capturedInstructor = instructorCaptor.getValue();
        assertEquals("Jane Updated", capturedInstructor.getFirstName());
        assertEquals("Doe Updated", capturedInstructor.getLastName());
    }

    @Test
    public void shouldRetrieveInstructorById() {
        // Arrange
        Instructor instructor = createSampleInstructor("Michael", "Johnson", LocalDate.of(2018, 3, 25));
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.of(instructor));

        // Act
        Instructor retrievedInstructor = instructorServices.retrieveInstructor(1L);

        // Assert
        assertNotNull(retrievedInstructor, "Retrieved instructor should not be null");
        assertEquals("Michael", retrievedInstructor.getFirstName());
        assertEquals("Johnson", retrievedInstructor.getLastName());

        // Verify repository interaction
        verify(instructorRepository, times(1)).findById(anyLong());
    }

    // Helper method to create a sample instructor
    private Instructor createSampleInstructor(String firstName, String lastName, LocalDate dateOfHire) {
        Instructor instructor = new Instructor();
        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setDateOfHire(dateOfHire);
        return instructor;
    }
}
