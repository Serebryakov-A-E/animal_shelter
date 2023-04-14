package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.repository.AnimalRepository;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public void save(Animal animal) {
        animalRepository.save(animal);
    }
}
