package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ISubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Query("select s from Subscription s where s.typeSub = :typeS order by s.startDate")
    Set<Subscription> findByTypeSubOrderByStartDateAsc(@Param("typeS") TypeSubscription typeSub);

    List<Subscription> getSubscriptionsByStartDateBetween(LocalDate date1, LocalDate date2);

    @Query("select distinct s from Subscription s where s.endDate <= CURRENT_TIME order by s.endDate")
    List<Subscription> findDistinctOrderByEndDateAsc();


    @Query("select (sum(s.price))/(count(s)) from Subscription s where s.typeSub = ?1")
    Float recurringRevenueByTypeSubEquals(TypeSubscription typeSub);

    // Méthode à ajouter : Récupérer les abonnements expirant bientôt
    List<Subscription> getSubscriptionsByEndDateBetween(LocalDate startDate, LocalDate endDate);

    // Méthode à ajouter : Calculer le revenu total par dates
    @Query("select sum(s.price) from Subscription s where s.startDate between :startDate and :endDate")
    Float calculateTotalRevenueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Méthode à ajouter : Calculer la durée moyenne des abonnements
    @Query("select avg(s.endDate - s.startDate) from Subscription s")
    Float calculateAverageSubscriptionDuration();


}
