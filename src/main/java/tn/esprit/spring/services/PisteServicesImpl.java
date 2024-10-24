package tn.esprit.spring.services;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import tn.esprit.spring.dto.PisteDTO;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PisteServicesImpl implements  IPisteServices{

    private ISkierRepository skierRepository;
    private IPisteRepository pisteRepository;

    @Override
    public List<Piste> retrieveAllPistes() {
        return pisteRepository.findAll();
    }

    @Override
    public Piste addPiste(PisteDTO pisteDTO) {
        // Convert DTO to entity
        Piste piste = new Piste();
        piste.setNamePiste(pisteDTO.getNamePiste());
        piste.setColor(Color.valueOf(pisteDTO.getColor().toUpperCase()));
        piste.setLength(pisteDTO.getLength());
        piste.setSlope(pisteDTO.getSlope());

        // Save entity to the repository
        return pisteRepository.save(piste);
    }

    @Override
    public void removePiste(Long numPiste) {
        pisteRepository.deleteById(numPiste);
    }

    @Override
    public Piste retrievePiste(Long numPiste) {
        return pisteRepository.findById(numPiste).orElseThrow(() -> new EntityNotFoundException("Piste not found with ID: " + numPiste));
    }
    @Override
    public List<Piste> findPistesBySkierCity(String city) {
        return pisteRepository.findBySkiersCity(city);
    }
    @Override
    public List<Piste> findPistesByColor(Color color) {
        return pisteRepository.findByColor(color);
    }

    @Override
    public Piste addPisteAndAssignToSkier(Piste piste, Long skierId) {
        Skier skier = skierRepository.findById(skierId)
                .orElseThrow(() -> new EntityNotFoundException("Skier not found  ID: " + skierId));


        Piste savedPiste = pisteRepository.save(piste);

        // Assign the piste to the skier
        skier.getPistes().add(savedPiste);
        skierRepository.save(skier);

        return savedPiste;
    }

    @Override
    public Piste addPisteAndAssignToSkiers(Piste piste, List<Long> skierIds) {
        // Enregistrer la nouvelle piste
        Piste savedPiste = pisteRepository.save(piste);

        // Vérifier si les skieurs existent et les assigner à la piste
        for (Long skierId : skierIds) {
            Optional<Skier> skierOptional = skierRepository.findById(skierId);
            if (skierOptional.isPresent()) {
                Skier skier = skierOptional.get();
                skier.getPistes().add(savedPiste);
                skierRepository.save(skier); // Sauvegarder le skieur mis à jour
            } else {
                throw new EntityNotFoundException("Skier not found with ID: " + skierId);
            }
        }
        return savedPiste;
    }




}
