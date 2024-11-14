package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.InstructorServicesImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Optional: Activates a test profile if you have one
public class InstructorServicesImplTest {

    @Autowired
    private InstructorServicesImpl instructorServices;

    @MockBean
    private ICourseRepository courseRepository;

    @Test
    public void addInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructor.setDateOfHire(LocalDate.of(2020, 1, 15));

        Instructor savedInstructor = instructorServices.addInstructor(instructor);
        assertNotNull(savedInstructor);
        assertEquals("John", savedInstructor.getFirstName());
        assertEquals("Doe", savedInstructor.getLastName());
        assertEquals(LocalDate.of(2020, 1, 15), savedInstructor.getDateOfHire());
    }

    @Test
    public void retrieveAllInstructors() {
        List<Instructor> instructors = instructorServices.retrieveAllInstructors();
        assertNotNull(instructors);
        assertTrue(instructors.size() >= 1, "There should be at least one instructor in the database.");
    }

    @Test
    public void updateInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("Jane");
        instructor.setLastName("Smith");
        instructor.setDateOfHire(LocalDate.of(2019, 6, 10));
        Instructor savedInstructor = instructorServices.addInstructor(instructor);

        savedInstructor.setFirstName("Jane Updated");
        savedInstructor.setLastName("Smith Updated");

        Instructor updatedInstructor = instructorServices.updateInstructor(savedInstructor);
        assertEquals("Jane Updated", updatedInstructor.getFirstName());
        assertEquals("Smith Updated", updatedInstructor.getLastName());
    }

    @Test
    public void retrieveInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("Michael");
        instructor.setLastName("Johnson");
        instructor.setDateOfHire(LocalDate.of(2018, 3, 25));
        Instructor savedInstructor = instructorServices.addInstructor(instructor);

        Instructor retrievedInstructor = instructorServices.retrieveInstructor(savedInstructor.getNumInstructor());
        assertNotNull(retrievedInstructor);
        assertEquals(savedInstructor.getNumInstructor(), retrievedInstructor.getNumInstructor());
        assertEquals("Michael", retrievedInstructor.getFirstName());
        assertEquals("Johnson", retrievedInstructor.getLastName());
    }
}
