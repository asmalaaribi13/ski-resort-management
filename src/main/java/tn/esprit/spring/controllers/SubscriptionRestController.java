package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.SubscriptionDTO;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISubscriptionServices;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "\uD83D\uDC65 Subscription Management")
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionRestController {

    private final ISubscriptionServices subscriptionServices;

    @Operation(description = "Add Subscription ")
    @PostMapping("/add")
    public Subscription addSubscription(@RequestBody SubscriptionDTO subscriptionDTO){
        // Map DTO to entity
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.valueOf(subscriptionDTO.getType()));
        return subscriptionServices.addSubscription(subscription);
    }
    @Operation(description = "Retrieve Subscription by Id")
    @GetMapping("/get/{id-subscription}")
    public Subscription getById(@PathVariable("id-subscription") Long numSubscription){
        return subscriptionServices.retrieveSubscriptionById(numSubscription);
    }
    
    @Operation(description = "Retrieve Subscriptions by Type")
    @GetMapping("/all/{typeSub}")
    public Set<Subscription> getSubscriptionsByType(@PathVariable("typeSub")TypeSubscription typeSubscription){
        return subscriptionServices.getSubscriptionByType(typeSubscription);
    }
    @Operation(description = "Update Subscription ")
    public Subscription updateSubscription(@RequestBody SubscriptionDTO subscriptionDTO){
        // Map DTO to entity
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.valueOf(subscriptionDTO.getType()));
        return subscriptionServices.updateSubscription(subscription);
    }
    @Operation(description = "Retrieve Subscriptions created between two dates")
    @GetMapping("/all/{date1}/{date2}")
    public List<Subscription> getSubscriptionsByDates(@PathVariable("date1") LocalDate startDate,
                                                      @PathVariable("date2") LocalDate endDate){
        return subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);
    }

    @Operation(description = "Delete Subscription by Id")
    @DeleteMapping("/delete/{id-subscription}")
    public void deleteSubscription(@PathVariable("id-subscription") Long numSubscription){
        subscriptionServices.deleteSubscription(numSubscription);
    }

    @Operation(description = "Calculate Total Revenue between two dates")
    @GetMapping("/revenue/{startDate}/{endDate}")
    public Float calculateTotalRevenue(@PathVariable("startDate") LocalDate startDate,
                                       @PathVariable("endDate") LocalDate endDate) {
        return subscriptionServices.calculateTotalRevenue(startDate, endDate);
    }

    @Operation(description = "Find Subscriptions Expiring Soon")
    @GetMapping("/expiring-soon")
    public List<Subscription> findSubscriptionsExpiringSoon() {
        return subscriptionServices.findSubscriptionsExpiringSoon();
    }

    @Operation(description = "Calculate Average Subscription Duration")
    @GetMapping("/average-duration")
    public Float calculateAverageSubscriptionDuration() {
        return subscriptionServices.calculateAverageSubscriptionDuration();
    }
}


