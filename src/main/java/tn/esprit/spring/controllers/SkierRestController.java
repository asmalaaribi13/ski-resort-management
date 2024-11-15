package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skier added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Skier addSkier(@RequestBody SkierDTO skierDTO) {
        Skier skier = convertToEntity(skierDTO);
        return skierServices.addSkier(skier);
    }

    private Skier convertToEntity(SkierDTO skierDTO) {
        Skier skier = new Skier();
        skier.setNumSkier(skierDTO.getNumSkier());
        skier.setFirstName(skierDTO.getFirstName());
        skier.setLastName(skierDTO.getLastName());
        skier.setCity(skierDTO.getCity());
        skier.setDateOfBirth(skierDTO.getDateOfBirth());

        // Gestion de l'abonnement
        if (skierDTO.getTypeSubscription() != null && skierDTO.getStartDate() != null && skierDTO.getPrice() != null) {
            Subscription subscription = new Subscription();
            subscription.setTypeSub(skierDTO.getTypeSubscription());
            subscription.setStartDate(skierDTO.getStartDate());
            subscription.setPrice(skierDTO.getPrice());

            // Calcul de la date de fin selon le type
            switch (skierDTO.getTypeSubscription()) {
                case ANNUAL:
                    subscription.setEndDate(skierDTO.getStartDate().plusYears(1));
                    break;
                case SEMESTRIEL:
                    subscription.setEndDate(skierDTO.getStartDate().plusMonths(6));
                    break;
                case MONTHLY:
                    subscription.setEndDate(skierDTO.getStartDate().plusMonths(1));
                    break;
            }
            skier.setSubscription(subscription);
        } else if (skierDTO.getTypeSubscription() != null) {
            throw new IllegalArgumentException("Subscription details are incomplete");
        }
        return skier;
    }

    @Operation(description = "Add Skier And Assign To Course")
    @PostMapping("/addSkierAndAssignToCourse/{numCourse}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skier added and assigned to course successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Skier addSkierAndAssignToCourse(@RequestBody SkierDTO skierDTO,
                                           @PathVariable("numCourse") Long numCourse) {
        Skier skier = convertToEntity(skierDTO);
        return skierServices.addSkierAndAssignToCourse(skier, numCourse);
    }

    @Operation(description = "Assign Skier To Subscription")
    @PutMapping("/assignToSub/{numSkier}/{numSub}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skier successfully assigned to subscription"),
            @ApiResponse(responseCode = "404", description = "Skier or Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Skier assignToSubscription(@PathVariable("numSkier") Long numSkier,
                                      @PathVariable("numSub") Long numSub) {
        return skierServices.assignSkierToSubscription(numSkier, numSub);
    }

    @Operation(description = "Assign Skier To Piste")
    @PutMapping("/assignToPiste/{numSkier}/{numPiste}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skier successfully assigned to piste"),
            @ApiResponse(responseCode = "404", description = "Skier or Piste not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Skier assignToPiste(@PathVariable("numSkier") Long numSkier,
                               @PathVariable("numPiste") Long numPiste) {
        return skierServices.assignSkierToPiste(numSkier, numPiste);
    }

    @Operation(description = "Retrieve all Skiers")
    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all skiers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<SkierDTO> getAllSkiers() {
        return skierServices.retrieveAllSkiers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Retrieve Skier by Id")
    @GetMapping("/get/{numSkier}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved skier"),
            @ApiResponse(responseCode = "404", description = "Skier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public SkierDTO getById(@PathVariable("numSkier") Long numSkier) {
        return convertToDTO(skierServices.retrieveSkier(numSkier));
    }

    @Operation(description = "Retrieve Skiers By Subscription Type")
    @GetMapping("/getSkiersBySubscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved skiers by subscription type"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<SkierDTO> retrieveSkiersBySubscriptionType(@RequestParam("typeSubscription") TypeSubscription typeSubscription) {
        return skierServices.retrieveSkiersBySubscriptionType(typeSubscription).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Delete Skier by Id")
    @DeleteMapping("/delete/{numSkier}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skier successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Skier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void deleteById(@PathVariable("numSkier") Long numSkier) {
        skierServices.removeSkier(numSkier);
    }

    private SkierDTO convertToDTO(Skier skier) {
        SkierDTO skierDTO = new SkierDTO();
        skierDTO.setNumSkier(skier.getNumSkier());
        skierDTO.setFirstName(skier.getFirstName());
        skierDTO.setLastName(skier.getLastName());
        skierDTO.setCity(skier.getCity());
        skierDTO.setDateOfBirth(skier.getDateOfBirth());
        if (skier.getSubscription() != null) {
            skierDTO.setTypeSubscription(skier.getSubscription().getTypeSub());
            skierDTO.setStartDate(skier.getSubscription().getStartDate());
            skierDTO.setPrice(skier.getSubscription().getPrice());
            skierDTO.setEndDate(skier.getSubscription().getEndDate()); // Nouveau champ
        }
        return skierDTO;
    }


    @Operation(description = "Analyze Piste Usage By Age Group")
    @GetMapping("/analyzePisteUsageByAgeGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully analyzed piste usage by age group"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Map<String, Double> analyzePisteUsageByAgeGroup() {
        return skierServices.analyzePisteUsageByAgeGroup();
    }

    @Operation(description = "Analyze Skier Engagement")
    @GetMapping("/analyzeSkierEngagement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully analyzed skier engagement"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Map<String, Object> analyzeSkierEngagement() {
        return skierServices.analyzeSkierEngagement();
    }

    @Operation(description = "Find Top Spending Skiers")
    @GetMapping("/findTopSpendingSkiers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top spending skiers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<SkierDTO> findTopSpendingSkiers(@RequestParam("topN") int topN) {
        return skierServices.findTopSpendingSkiers(topN).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(description = "Get Average Age By Subscription Type")
    @GetMapping("/getAverageAgeBySubscriptionType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved average age by subscription type"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Map<TypeSubscription, Double> getAverageAgeBySubscriptionType() {
        return skierServices.getAverageAgeBySubscriptionType();
    }

    @Operation(description = "Calculate Total Spending By Skier")
    @GetMapping("/calculateTotalSpending/{numSkier}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated total spending for skier"),
            @ApiResponse(responseCode = "404", description = "Skier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Float calculateTotalSpendingBySkier(@PathVariable("numSkier") Long numSkier) {
        return skierServices.calculateTotalSpendingBySkier(numSkier);
    }
}
