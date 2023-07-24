package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.AnimalType;
import me.serebryakov.animal_shelter.entity.Owner;

import java.util.List;

public interface AnimalService {
    void save(Animal animal);

    Animal getById(long id);

    void updateOwnerById(long id, Owner owner);

    List<Animal> getAnimalsByOwner(Owner owner);

    List<Animal> findAnimalsByOwnerAndAnimalType(Owner owner, AnimalType animalType);

    void delete(long id);
}
