package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.SkierDTO;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.services.ISkierServices;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "\uD83C\uDFC2 Skier Management")
@RestController
@RequestMapping("/skier")
@RequiredArgsConstructor
public class SkierRestController {

    private final ISkierServices skierServices;

    @Operation(description = "Add Skier")
    @PostMapping("/add")
    public Skier addSkier(@RequestBody SkierDTO skierDTO) {
        Skier skier = convertToEntity(skierDTO);
        return skierServices.addSkier(skier);
    }

    private Skier convertToEntity(SkierDTO skierDTO) {
        Skier skier = new Skier();
        skier.setNumSkier(skierDTO.getId());
        skier.setFirstName(skierDTO.getFirstName());
        skier.setLastName(skierDTO.getLastName());
        skier.setCity(skierDTO.getCity());

        if (skierDTO.getTypeSubscription() != null) {
            TypeSubscription typeSub = TypeSubscription.valueOf(skierDTO.getTypeSubscription().name());
            Subscription subscription = new Subscription();
            subscription.setTypeSub(typeSub);
            skier.setSubscription(subscription);
        }

        return skier;
    }

    @Operation(description = "Add Skier And Assign To Course")
    @PostMapping("/addAndAssign/{numCourse}")
    public Skier addSkierAndAssignToCourse(@RequestBody SkierDTO skierDTO,
                                           @PathVariable("numCourse") Long numCourse) {
        Skier skier = convertToEntity(skierDTO);
        return skierServices.addSkierAndAssignToCourse(skier, numCourse);
    }

    @Operation(description = "Assign Skier To Subscription")
    @PutMapping("/assignToSub/{numSkier}/{numSub}")
    public Skier assignToSubscription(@PathVariable("numSkier") Long numSkier,
                                      @PathVariable("numSub") Long numSub) {
        return skierServices.assignSkierToSubscription(numSkier, numSub);
    }

    @Operation(description = "Assign Skier To Piste")
    @PutMapping("/assignToPiste/{numSkier}/{numPiste}")
    public Skier assignToPiste(@PathVariable("numSkier") Long numSkier,
                               @PathVariable("numPiste") Long numPiste) {
        return skierServices.assignSkierToPiste(numSkier, numPiste);
    }

    @Operation(description = "Retrieve all Skiers")
    @GetMapping("/all")
    public List<SkierDTO> getAllSkiers() {
        return skierServices.retrieveAllSkiers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Retrieve Skier by Id")
    @GetMapping("/get/{numSkier}")
    public SkierDTO getById(@PathVariable("numSkier") Long numSkier) {
        return convertToDTO(skierServices.retrieveSkier(numSkier));
    }

    @Operation(description = "Retrieve Skiers By Subscription Type")
    @GetMapping("/getSkiersBySubscription")
    public List<SkierDTO> retrieveSkiersBySubscriptionType(@RequestParam("typeSubscription") TypeSubscription typeSubscription) {
        return skierServices.retrieveSkiersBySubscriptionType(typeSubscription).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Delete Skier by Id")
    @DeleteMapping("/delete/{numSkier}")
    public void deleteById(@PathVariable("numSkier") Long numSkier) {
        skierServices.removeSkier(numSkier);
    }

    private SkierDTO convertToDTO(Skier skier) {
        SkierDTO skierDTO = new SkierDTO();
        skierDTO.setId(skier.getNumSkier());
        skierDTO.setFirstName(skier.getFirstName());
        skierDTO.setLastName(skier.getLastName());
        skierDTO.setCity(skier.getCity());
        if (skier.getSubscription() != null) {
            skierDTO.setTypeSubscription(skier.getSubscription().getTypeSub());
        }
        return skierDTO;
    }

    @Operation(description = "Analyze Piste Usage By Age Group")
    @GetMapping("/analyzePisteUsageByAgeGroup")
    public Map<String, Double> analyzePisteUsageByAgeGroup() {
        return skierServices.analyzePisteUsageByAgeGroup();
    }

    @Operation(description = "Analyze Skier Engagement")
    @GetMapping("/analyzeSkierEngagement")
    public Map<String, Object> analyzeSkierEngagement() {
        return skierServices.analyzeSkierEngagement();
    }

    @Operation(description = "Find Top Spending Skiers")
    @GetMapping("/findTopSpendingSkiers")
    public List<SkierDTO> findTopSpendingSkiers(@RequestParam("topN") int topN) {
        return skierServices.findTopSpendingSkiers(topN).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Get Average Age By Subscription Type")
    @GetMapping("/getAverageAgeBySubscriptionType")
    public Map<TypeSubscription, Double> getAverageAgeBySubscriptionType() {
        return skierServices.getAverageAgeBySubscriptionType();
    }

    @Operation(description = "Calculate Total Spending By Skier")
    @GetMapping("/calculateTotalSpending/{numSkier}")
    public Float calculateTotalSpendingBySkier(@PathVariable("numSkier") Long numSkier) {
        return skierServices.calculateTotalSpendingBySkier(numSkier);
    }
}
