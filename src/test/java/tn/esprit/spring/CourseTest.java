package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.CourseServicesImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CourseTest {
    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testFindCoursesByType() {
        // Create a course and set its type
        Course course = new Course();
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN); // Use the correct setter method

        // Mock the repository behavior
        when(courseRepository.findByTypeCourse(TypeCourse.COLLECTIVE_CHILDREN)).thenReturn(Collections.singletonList(course));

        // Call the service method
        List<Course> childrenCourses = courseServices.findCoursesByType(TypeCourse.COLLECTIVE_CHILDREN);

        // Assertions
        assertNotNull(childrenCourses); // Verify that the list is not null
        assertEquals(1, childrenCourses.size()); // Verify that there is one course
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, childrenCourses.get(0).getTypeCourse()); // Check that the type is COLLECTIVE_CHILDREN
        verify(courseRepository, times(1)).findByTypeCourse(TypeCourse.COLLECTIVE_CHILDREN); // Verify that the repository was called once
    }


}
