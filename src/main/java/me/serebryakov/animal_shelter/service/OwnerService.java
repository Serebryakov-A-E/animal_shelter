package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.Owner;
import me.serebryakov.animal_shelter.exception.OwnerNotExistException;
import me.serebryakov.animal_shelter.repository.OwnerRepository;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {
    private final OwnerRepository repository;


    public OwnerService(OwnerRepository repository) {
        this.repository = repository;
    }

    public void save(Owner owner) {
        repository.save(owner);
    }

    public Owner getById(long id) {
        return repository.findById(id).orElseThrow(OwnerNotExistException::new);
    }

    public void updateAnimalById(long id, Animal animal) {
        Owner owner = repository.findById(id).orElseThrow(OwnerNotExistException::new);
        owner.setAnimal(animal);
        save(owner);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
