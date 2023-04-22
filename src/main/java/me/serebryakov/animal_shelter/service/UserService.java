package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.User;
import me.serebryakov.animal_shelter.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void create(long chatId) {
        User user = new User();
        user.setChatId(chatId);
        repository.save(user);
    }

    public User getUserByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    public void updateShelterId(long chatId, int shelterId) {
        User user = repository.findByChatId(chatId);
        user.setShelterId(shelterId);
        repository.save(user);
    }

    public void updateMenuLevel(long chatId, int menuLevel) {
        User user = repository.findByChatId(chatId);
        user.setLastMenuLevel(menuLevel);
        repository.save(user);
    }
}
