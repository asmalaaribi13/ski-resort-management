package tn.esprit.spring.services;

import tn.esprit.spring.entities.*;
import java.util.List;
import java.util.Map;

public interface ISkierServices {

	Skier  addSkier(Skier  skier);
	Skier assignSkierToSubscription(Long numSkier, Long numSubscription);
	Skier addSkierAndAssignToCourse(Skier skier, Long numCourse);
	void removeSkier (Long numSkier);
	Skier retrieveSkier (Long numSkier);
	List<Skier> retrieveAllSkiers();
	Skier assignSkierToPiste(Long numSkieur, Long numPiste);
	List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription);

	/*------------------------------------------------------------------------------------------------*/

	Map<String, Double> analyzePisteUsageByAgeGroup();

	Map<String, Object> analyzeSkierEngagement();

	List<Skier> findTopSpendingSkiers(int topN);

	Map<TypeSubscription, Double> getAverageAgeBySubscriptionType();

	Float calculateTotalSpendingBySkier(Long numSkier);



}
