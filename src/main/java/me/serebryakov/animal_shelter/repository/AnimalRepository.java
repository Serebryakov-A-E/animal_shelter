package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

}
