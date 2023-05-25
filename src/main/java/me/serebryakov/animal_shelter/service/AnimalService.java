package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.AnimalType;
import me.serebryakov.animal_shelter.entity.Owner;
import me.serebryakov.animal_shelter.exception.AnimalNotExistException;
import me.serebryakov.animal_shelter.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {
    private final AnimalRepository repository;

    public AnimalService(AnimalRepository animalRepository) {
        this.repository = animalRepository;
    }

    public void save(Animal animal) {
        repository.save(animal);
    }

    public Animal getById(long id) {
        return repository.findById(id).orElseThrow(AnimalNotExistException::new);
    }

    public void updateOwnerById(long id, Owner owner) {
        Animal animal = repository.findById(id).orElseThrow(AnimalNotExistException::new);
        animal.setOwner(owner);
        save(animal);
    }

    public List<Animal> getAnimalsByOwner(Owner owner) {
        return repository.findAnimalsByOwner(owner);
    }

    public List<Animal> findAnimalsByOwnerAndAnimalType(Owner owner, AnimalType animalType) {
        return repository.findAnimalsByOwnerAndAnimalType(owner, animalType);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
