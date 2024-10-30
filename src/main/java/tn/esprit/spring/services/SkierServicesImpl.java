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
    private IInstructorRepository instructorRepository;

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
    public List<Skier> findSkiersByPisteColor(Color color) {
        return skierRepository.findAll().stream()
                .filter(skier -> skier.getPistes().stream()
                        .anyMatch(piste -> piste.getColor() == color))
                .collect(Collectors.toList());
    }

    /* Get Total Spending by Skier: Calculate the total amount
    spent by a skier, including course fees and subscription costs.*/
    @Override
    public Float calculateTotalSpendingBySkier(Long numSkier) {
        Skier skier = skierRepository.findById(numSkier)
                .orElseThrow(() -> new IllegalArgumentException(SKIER_NOT_FOUND));

        Float courseCost = skier.getRegistrations().stream()
                .map(registration -> registration.getCourse().getPrice())
                .reduce(0f, Float::sum);

        Float subscriptionCost = skier.getSubscription() != null ? skier.getSubscription().getPrice() : 0f;

        return courseCost + subscriptionCost;
    }

    @Override
    public List<Skier> findSkiersWithHighestAverageCoursePrice(int topN) {
        return skierRepository.findAll().stream()
                .map(skier -> new AbstractMap.SimpleEntry<>(skier,
                        skier.getRegistrations().stream()
                                .mapToDouble(registration -> registration.getCourse().getPrice())
                                .average()
                                .orElse(0.0)))
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Skier> findSkiersActiveInMultipleCourseTypes(int minTypes) {
        return skierRepository.findAll().stream()
                .filter(skier -> {
                    Set<TypeCourse> courseTypes = skier.getRegistrations().stream()
                            .map(registration -> registration.getCourse().getTypeCourse())
                            .collect(Collectors.toSet());
                    return courseTypes.size() >= minTypes;
                })
                .collect(Collectors.toList());
    }

    /* Analyze Piste Usage Across Skiers by Age Group */
    @Override
    public Map<String, Double> analyzePisteUsageByAgeGroup() {
        LocalDate currentDate = LocalDate.now();
        Map<String, List<Integer>> ageGroups = new HashMap<>();
        ageGroups.put("Children (0-12)", new ArrayList<>());
        ageGroups.put("Teens (13-19)", new ArrayList<>());
        ageGroups.put("Adults (20-59)", new ArrayList<>());
        ageGroups.put("Seniors (60+)", new ArrayList<>());

        // Group skiers by age
        skierRepository.findAll().forEach(skier -> {
            int age = currentDate.getYear() - skier.getDateOfBirth().getYear();
            if (age <= 12) {
                ageGroups.get("Children (0-12)").add(skier.getPistes().size());
            } else if (age <= 19) {
                ageGroups.get("Teens (13-19)").add(skier.getPistes().size());
            } else if (age <= 59) {
                ageGroups.get("Adults (20-59)").add(skier.getPistes().size());
            } else {
                ageGroups.get("Seniors (60+)").add(skier.getPistes().size());
            }
        });

        // Calculate average piste usage per age group
        Map<String, Double> averagePisteUsage = new HashMap<>();
        ageGroups.forEach((group, usage) -> {
            double average = usage.stream().mapToInt(i -> i).average().orElse(0.0);
            averagePisteUsage.put(group, average);
        });

        return averagePisteUsage;
    }
}
