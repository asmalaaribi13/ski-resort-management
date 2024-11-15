package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.entities.TypeSubscription;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("squid:S1068") // Suppression de l'avertissement pour les champs inutilisés
public class SkierDTO {

    // Champs basiques du skieur
    private Long numSkier;
    private String firstName;
    private String lastName;
    private String city;
    private LocalDate dateOfBirth;

    // Détails sur l'abonnement
    private TypeSubscription typeSubscription;
    private LocalDate endDate;
    private LocalDate startDate;
    private Float price;
}
