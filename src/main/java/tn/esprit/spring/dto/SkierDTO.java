package tn.esprit.spring.dto;

import lombok.Data;
import tn.esprit.spring.entities.TypeSubscription;

@Data
public class SkierDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String city;
    private TypeSubscription typeSubscription; // Adapté pour contenir les infos nécessaires
    private Float totalSpending; // Si pertinent dans vos méthodes de services
}
