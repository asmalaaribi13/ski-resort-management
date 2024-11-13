package tn.esprit.spring.dto;

import java.time.LocalDate;
import lombok.Data;
import tn.esprit.spring.entities.TypeSubscription;

@Data
@SuppressWarnings("squid:S1068") // S1068 est souvent l'erreur pour champs inutilisés
public class SkierDTO {
    private Long numSkier;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String city;
    private TypeSubscription typeSubscription;
    private LocalDate startDate;
    private Float price;
    private Float totalSpending;
}

