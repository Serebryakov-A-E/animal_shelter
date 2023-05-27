package me.serebryakov.animal_shelter.service;

import com.pengrad.telegrambot.model.Contact;
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

    public void create(long chatId) {
        Owner owner = new Owner();
        owner.setChatId(chatId);
        repository.save(owner);
    }

    public Owner getById(long id) {
        return repository.findById(id).orElseThrow(OwnerNotExistException::new);
    }

    public Owner getByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    /*
    public void updateAnimalById(long id, Animal animal) {
        Owner owner = repository.findById(id).orElseThrow(OwnerNotExistException::new);
        owner.setAnimal(animal);
        save(owner);
    }

     */

    public void addAnimalByChatId(long chatId, Animal animal) {
        Owner owner = repository.findByChatId(chatId);
        if (owner == null) {
            throw new OwnerNotExistException();
        }
        owner.getAnimalList().add(animal);
        save(owner);
    }

    public void saveContacts(long chatId, Contact contact) {
        Owner owner = repository.findByChatId(chatId);
        owner.setPhoneNumber(contact.phoneNumber());
        owner.setName(contact.firstName());
        save(owner);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}
