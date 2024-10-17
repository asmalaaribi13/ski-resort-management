package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.CourseServicesImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GestionStationSkiApplicationTests {

	@Mock
	private ICourseRepository courseRepository;

	@InjectMocks
	private CourseServicesImpl courseServices;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRetrieveAllCourses() {
		// Configurer les données de test
		Course course = new Course();
		course.setNumCourse(1L);
		course.setLevel(2);
		course.setPrice(150.0f);

		when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));

		// Appeler la méthode à tester
		List<Course> courses = courseServices.retrieveAllCourses();

		// Vérifications
		assertNotNull(courses);
		assertEquals(1, courses.size());
		assertEquals(1L, courses.get(0).getNumCourse());
		verify(courseRepository, times(1)).findAll();
	}
}
