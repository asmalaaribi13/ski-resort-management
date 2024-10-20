package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.services.RegistrationServicesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.Optional;

public class RegistrationTest {

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testAddRegistrationToAFullCourse() {
        Registration registration = new Registration();
        registration.setNumWeek(1);

        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setDateOfBirth(LocalDate.of(2010, 1, 1));

        Course course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countByCourseAndNumWeek(course, 1)).thenReturn(6L);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);
        verify(registrationRepository, never()).save(any(Registration.class));
    }
    @Test
    void testInvalidSkierAgeForChildrenCourse() {
        Registration registration = new Registration();
        registration.setNumWeek(1);

        Skier skier = new Skier();
        skier.setNumSkier(1L);
        skier.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Course course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);
        verify(registrationRepository, never()).save(any(Registration.class));
    }
    @Test
    void testSkierAlreadyRegisteredForCourseAndWeek() {
        Registration registration = new Registration();
        registration.setNumWeek(1);

        Skier skier = new Skier();
        skier.setNumSkier(1L);

        Course course = new Course();
        course.setNumCourse(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(1, 1L, 1L)).thenReturn(1L);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);  
        verify(registrationRepository, never()).save(any(Registration.class));
    }
    @Test
    void testAddRegistrationAndAssignToSkier() {
        Registration registration = new Registration();
        Skier skier = new Skier();
        skier.setNumSkier(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        assertNotNull(result);
        assertEquals(skier, result.getSkier());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse() {
        try{
            Registration registration = new Registration();
            registration.setNumWeek(1);

            Skier skier = new Skier();
            skier.setNumSkier(1L);
            skier.setDateOfBirth(LocalDate.of(2010, 1, 1));

            Course course = new Course();
            course.setNumCourse(1L);
            course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);

            when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
            when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
            when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(1, 1L, 1L)).thenReturn(0L);
            when(registrationRepository.countByCourseAndNumWeek(course, 1)).thenReturn(0L);
            when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

            Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

            assertNotNull(result);
            verify(registrationRepository, times(1)).save(registration);
        }
        catch (Exception e) {
            final Logger log = LoggerFactory.getLogger(RegistrationServicesImpl.class);
            log.error("Error during registration: ", e);
            throw e;
        }
    }


}
