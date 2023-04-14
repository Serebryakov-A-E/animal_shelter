package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Owner;
import me.serebryakov.animal_shelter.entity.Report;
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
}
