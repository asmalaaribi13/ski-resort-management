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

	Map<String, Object> analyzeSkierEngagement();

	List<Skier> findSkiersWithMultipleSupports();

	List<Skier> findSkiersByPisteColor(Color color);

	/* Get Total Spending by Skier: Calculate the total amount
		spent by a skier, including course fees and subscription costs.*/
	Float calculateTotalSpendingBySkier(Long numSkier);

	List<Skier> findSkiersWithHighestAverageCoursePrice(int topN);

	List<Skier> findSkiersActiveInMultipleCourseTypes(int minTypes);

	/* Analyze Piste Usage Across Skiers by Age Group */
	Map<String, Double> analyzePisteUsageByAgeGroup();

	/*-----------------------------------------------------------------------------------------------*/



}
