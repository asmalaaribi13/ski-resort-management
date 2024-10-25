package tn.esprit.spring.services;

import tn.esprit.spring.entities.*;

import java.util.List;
import java.util.Map;

public interface ISkierServices {

	List<Skier> retrieveAllSkiers();
	Skier  addSkier(Skier  skier);
	Skier assignSkierToSubscription(Long numSkier, Long numSubscription);
	Skier addSkierAndAssignToCourse(Skier skier, Long numCourse);
	void removeSkier (Long numSkier);
	Skier retrieveSkier (Long numSkier);
	Skier assignSkierToPiste(Long numSkieur, Long numPiste);
	List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);

	/*-----------------------------------------------------------------------------------------------*/

	Skier findMostActiveSkier();

	int calculateTotalCourseDurationForSkier(Long numSkier);


	Map<String, Object> analyzeSkierEngagement();


	List<Skier> findSkiersWithMultipleSupports();

	List<Skier> findSkiersByAgeRange(int minAge, int maxAge);
}
