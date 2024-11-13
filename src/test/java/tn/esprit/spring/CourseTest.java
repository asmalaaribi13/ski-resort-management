package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.CourseServicesImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Add this annotation for JUnit 5 support
public class CourseTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    @BeforeEach
    void setUp() {
        // No need for MockitoAnnotations.openMocks(this) as @ExtendWith handles this
    }

    @Test
    void testFindCoursesByType() {
        // Create a course and set its type
        Course course = new Course();
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        // Mock the repository behavior
        when(courseRepository.findByTypeCourse(TypeCourse.COLLECTIVE_CHILDREN))
                .thenReturn(Collections.singletonList(course));

        // Call the service method
        List<Course> childrenCourses = courseServices.findCoursesByType(TypeCourse.COLLECTIVE_CHILDREN);

        // Assertions
        assertNotNull(childrenCourses);
        assertEquals(1, childrenCourses.size());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, childrenCourses.get(0).getTypeCourse());

        // Verify that the repository's method was called once
        verify(courseRepository, times(1)).findByTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
    }
}
