package tn.esprit.spring.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import tn.esprit.spring.entities.TypeSubscription;

@Getter
@Setter
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
