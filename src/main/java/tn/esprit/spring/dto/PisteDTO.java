package tn.esprit.spring.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PisteDTO {

    private String namePiste;
    private String color;
    private int length;
    private int slope;
}
