package me.serebryakov.animal_shelter.service;

import com.pengrad.telegrambot.model.Contact;
import me.serebryakov.animal_shelter.entity.Animal;
import me.serebryakov.animal_shelter.entity.Owner;

public interface OwnerService {
    void save(Owner owner);

    void create(long chatId);

    Owner getById(long id);

    Owner getByChatId(long chatId);

    void addAnimalByChatId(long chatId, Animal animal);

    void saveContacts(long chatId, Contact contact);

    void delete(long id);
}
