package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.UserStatus;
import me.serebryakov.animal_shelter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void create(long chatId) {
        UserStatus user = new UserStatus();
        user.setChatId(chatId);
        repository.save(user);
    }

    public UserStatus getUserByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    public void updateShelterId(long chatId, int shelterId) {
        UserStatus user = repository.findByChatId(chatId);
        user.setShelterId(shelterId);
        repository.save(user);
    }

    public void updateReportStatus(long chatId, boolean status) {
        UserStatus user = repository.findByChatId(chatId);
        user.setIsSendingReport(status);
        repository.save(user);
    }

    public void updateMenuLevel(long chatId, int menuLevel) {
        UserStatus user = repository.findByChatId(chatId);
        user.setLastMenuLevel(menuLevel);
        repository.save(user);
    }

    public void updateLastInfoId(long chatId, int lastInfoId) {
        UserStatus user = repository.findByChatId(chatId);
        user.setLastInfoId(lastInfoId);
        repository.save(user);
    }

    public List<UserStatus> getAllUsers() {
        return repository.findAll();
    }
}
