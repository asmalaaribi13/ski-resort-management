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
    private Long numSkier;          // Identifiant unique du skieur
    private String firstName;       // Prénom
    private String lastName;        // Nom de famille
    private String city;            // Ville de résidence
    private LocalDate dateOfBirth;  // Date de naissance

    // Détails sur l'abonnement
    private TypeSubscription typeSubscription; // Type d'abonnement (ANNUAL, SEMESTRIEL, MONTHLY)
    private LocalDate startDate;               // Date de début de l'abonnement
    private Float price;                       // Prix de l'abonnement
}
