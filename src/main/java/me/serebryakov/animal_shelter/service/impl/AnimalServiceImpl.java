package me.serebryakov.animal_shelter.service.impl;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.AnimalType;
import me.serebryakov.animal_shelter.entity.Owner;
import me.serebryakov.animal_shelter.exception.AnimalNotExistException;
import me.serebryakov.animal_shelter.repository.AnimalRepository;
import me.serebryakov.animal_shelter.service.AnimalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository repository;

    public AnimalServiceImpl(AnimalRepository animalRepository) {
        this.repository = animalRepository;
    }

    @Override
    public void save(Animal animal) {
        repository.save(animal);
    }

    @Override
    public Animal getById(long id) {
        return repository.findById(id).orElseThrow(AnimalNotExistException::new);
    }

    @Override
    public void updateOwnerById(long id, Owner owner) {
        Animal animal = repository.findById(id).orElseThrow(AnimalNotExistException::new);
        animal.setOwner(owner);
        save(animal);
    }

    @Override
    public List<Animal> getAnimalsByOwner(Owner owner) {
        return repository.findAnimalsByOwner(owner);
    }

    @Override
    public List<Animal> findAnimalsByOwnerAndAnimalType(Owner owner, AnimalType animalType) {
        return repository.findAnimalsByOwnerAndAnimalType(owner, animalType);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
