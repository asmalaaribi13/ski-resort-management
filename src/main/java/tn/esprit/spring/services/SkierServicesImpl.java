package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    // Constante pour éviter la duplication de "Skier not found"
    private static final String SKIER_NOT_FOUND = "Skier not found";

    private ISkierRepository skierRepository;
    private IPisteRepository pisteRepository;
    private ICourseRepository courseRepository;
    private IRegistrationRepository registrationRepository;
    private ISubscriptionRepository subscriptionRepository;

    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    @Override
    public Skier addSkier(Skier skier) {
        switch (skier.getSubscription().getTypeSub()) {
            case ANNUAL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                break;
            case SEMESTRIEL:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                break;
            case MONTHLY:
                skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                break;
        }
        return skierRepository.save(skier);
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Skier skier = skierRepository.findById(numSkier).orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));
        Subscription subscription = subscriptionRepository.findById(numSubscription).orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        skier.setSubscription(subscription);
        return skierRepository.save(skier);
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.getById(numCourse);
        Set<Registration> registrations = savedSkier.getRegistrations();
        for (Registration r : registrations) {
            r.setSkier(savedSkier);
            r.setCourse(course);
            registrationRepository.save(r);
        }
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier).orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        Skier skier = skierRepository.findById(numSkieur).orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));
        Piste piste = pisteRepository.findById(numPiste).orElseThrow(() -> new IllegalArgumentException("Piste not found"));

        Set<Piste> pisteList = skier.getPistes();
        if (pisteList == null) {
            pisteList = new HashSet<>();
        }
        pisteList.add(piste);
        skier.setPistes(pisteList);

        return skierRepository.save(skier);
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }

    /*-------------------------------------------------------------------------------------------*/

    @Override
    public Map<String, Object> analyzeSkierEngagement() {
        List<Skier> skiers = skierRepository.findAll();
        Map<String, Object> statistics = new HashMap<>();

        double averageCoursesPerSkier = skiers.stream()
                .mapToInt(skier -> skier.getRegistrations().size())
                .average()
                .orElse(0.0);

        Skier mostActiveSkier = skiers.stream()
                .max(Comparator.comparingInt(skier -> skier.getRegistrations().size()))
                .orElse(null);

        statistics.put("averageCoursesPerSkier", averageCoursesPerSkier);
        statistics.put("mostActiveSkier", mostActiveSkier);

        return statistics;
    }

    @Override
    public List<Skier> findSkiersWithMultipleSupports() {
        List<Skier> skiers = skierRepository.findAll();

        return skiers.stream()
                .filter(skier -> {
                    Set<Support> supports = skier.getRegistrations().stream()
                            .map(registration -> registration.getCourse().getSupport())
                            .collect(Collectors.toSet());
                    return supports.size() > 1;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Skier> findSkiersByAgeRange(int minAge, int maxAge) {
        LocalDate currentDate = LocalDate.now();

        return skierRepository.findAll().stream()
                .filter(skier -> {
                    int age = currentDate.getYear() - skier.getDateOfBirth().getYear();
                    return age >= minAge && age <= maxAge;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Skier findMostActiveSkier() {
        return skierRepository.findAll().stream()
                .max(Comparator.comparingInt(skier -> skier.getRegistrations().size()))
                .orElse(null);
    }

    @Override
    public int calculateTotalCourseDurationForSkier(Long numSkier) {
        Skier skier = skierRepository.findById(numSkier).orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));

        return skier.getRegistrations().stream()
                .mapToInt(reg -> reg.getCourse().getTimeSlot())
                .sum();
    }


}
