package me.serebryakov.animal_shelter.service.impl;

import com.pengrad.telegrambot.model.Contact;
import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.Owner;
import me.serebryakov.animal_shelter.exception.OwnerNotExistException;
import me.serebryakov.animal_shelter.repository.OwnerRepository;
import me.serebryakov.animal_shelter.service.OwnerService;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository repository;


    public OwnerServiceImpl(OwnerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Owner owner) {
        repository.save(owner);
    }

    @Override
    public void create(long chatId) {
        Owner owner = new Owner();
        owner.setChatId(chatId);
        repository.save(owner);
    }

    @Override
    public Owner getById(long id) {
        return repository.findById(id).orElseThrow(OwnerNotExistException::new);
    }

    @Override
    public Owner getByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    @Override
    public void addAnimalByChatId(long chatId, Animal animal) {
        Owner owner = repository.findByChatId(chatId);
        if (owner == null) {
            throw new OwnerNotExistException();
        }
        owner.getAnimalList().add(animal);
        save(owner);
    }

    @Override
    public void saveContacts(long chatId, Contact contact) {
        Owner owner = repository.findByChatId(chatId);
        owner.setPhoneNumber(contact.phoneNumber());
        owner.setName(contact.firstName());
        save(owner);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}
