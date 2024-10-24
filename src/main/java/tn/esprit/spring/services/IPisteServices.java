package tn.esprit.spring.services;


import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;

import java.util.List;

public interface IPisteServices {

    List<Piste> retrieveAllPistes();

    Piste  addPiste(Piste  piste);

    void removePiste (Long numPiste);

    List<Piste> findPistesByColor(Color color);

    List<Piste> findPistesBySkierCity(String city);

    Piste retrievePiste (Long numPiste);

    Piste addPisteAndAssignToSkier(Piste piste, Long skierId);

    Piste addPisteAndAssignToSkiers(Piste piste, List<Long> skierIds);

     List<Piste> findPistesForSkierByAge(Long skierId);

     List<Piste> findPopularPistes(int minCourses);

}
