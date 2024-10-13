package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;

import java.util.List;

public interface IPisteRepository extends JpaRepository<Piste, Long> {
    List<Piste> findByColor(Color color);

    List<Piste> findBySkiersCity(String city);
}
