package tn.esprit.spring.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;

import java.util.List;

@Tag(name = "\uD83C\uDFBF Piste Management")
@RestController
@RequestMapping("/piste")
@RequiredArgsConstructor
public class PisteRestController {

    private final IPisteServices pisteServices;

    @Operation(description = "Add Piste")
    @PostMapping("/add")
    public Piste addPiste(@RequestBody PisteDTO piste){
        return  pisteServices.addPiste(piste);
    }
    @Operation(description = "Retrieve all Pistes")
    @GetMapping("/all")
    public List<Piste> getAllPistes(){
        return pisteServices.retrieveAllPistes();
    }

    @Operation(description = "Retrieve Piste by Id")
    @GetMapping("/get/{id-piste}")
    public Piste getById(@PathVariable("id-piste") Long numPiste){
        return pisteServices.retrievePiste(numPiste);
    }

    @Operation(description = "Delete Piste by Id")
    @DeleteMapping("/delete/{id-piste}")
    public void deleteById(@PathVariable("id-piste") Long numPiste){
        pisteServices.removePiste(numPiste);
    }

    @GetMapping("/by-city")
    public ResponseEntity<List<Piste>> getPistesByCity(@RequestParam String city) {
        List<Piste> pistes = pisteServices.findPistesBySkierCity(city);
        return ResponseEntity.ok(pistes);
    }

    @GetMapping("/by-color")
    public ResponseEntity<List<Piste>> getPistesByColor(@RequestParam Color color) {
        List<Piste> pistes = pisteServices.findPistesByColor(color);
        return ResponseEntity.ok(pistes);
    }


    @GetMapping("/popular")
    public ResponseEntity<List<Piste>> findPopularPistes(@RequestParam int minCourses) {
        List<Piste> popularPistes = pisteServices.findPopularPistes(minCourses);
        return popularPistes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(popularPistes);
    }

    // Nouvelle méthode pour trouver les pistes en fonction de l'âge d'un skieur
    @GetMapping("/skier/{skierId}/pistes-by-age")
    public ResponseEntity<List<Piste>> findPistesForSkierByAge(@PathVariable Long skierId) {
        try {
            List<Piste> pistes = pisteServices.findPistesForSkierByAge(skierId);
            return pistes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pistes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
