package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.AnimalType;
import me.serebryakov.animal_shelter.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    public List<Animal> findAnimalsByOwner(Owner owner);

    public List<Animal> findAnimalsByOwnerAndAnimalType(Owner owner, AnimalType animalType);

}
