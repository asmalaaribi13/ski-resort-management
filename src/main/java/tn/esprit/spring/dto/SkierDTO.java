package tn.esprit.spring.dto;

import java.time.LocalDate;
import lombok.Data;
import tn.esprit.spring.entities.TypeSubscription;

@Data
public class SkierDTO {
    private Long numSkier;           // Le numéro du skieur, peut être généré par la base
    private String firstName;        // Prénom du skieur
    private String lastName;         // Nom du skieur
    private LocalDate dateOfBirth;   // Date de naissance du skieur (optionnelle)
    private String city;             // Ville du skieur

    private TypeSubscription typeSubscription; // Type d'abonnement (ANNUAL, SEMESTRIEL, MONTHLY)
    private LocalDate startDate;     // Date de début de l'abonnement
    private Float price;             // Prix de l'abonnement
    private Float totalSpending;     // Optionnel, calculé côté serveur si nécessaire
}
