package me.serebryakov.animal_shelter.service.impl;

import me.serebryakov.animal_shelter.entity.UserStatus;
import me.serebryakov.animal_shelter.repository.UserRepository;
import me.serebryakov.animal_shelter.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;


    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(long chatId) {
        UserStatus user = new UserStatus();
        user.setChatId(chatId);
        repository.save(user);
    }
    @Override
    public void save(UserStatus user) {
        repository.save(user);
    }

    @Override
    public UserStatus getUserByChatId(long chatId) {
        return repository.findByChatId(chatId);
    }

    @Override
    public void updateShelterId(long chatId, int shelterId) {
        UserStatus user = repository.findByChatId(chatId);
        user.setShelterId(shelterId);
        repository.save(user);
    }

    @Override
    public void updateReportStatus(long chatId, boolean status) {
        UserStatus user = repository.findByChatId(chatId);
        user.setIsSendingReport(status);
        repository.save(user);
    }

    @Override
    public void updateMenuLevel(long chatId, int menuLevel) {
        UserStatus user = repository.findByChatId(chatId);
        user.setLastMenuLevel(menuLevel);
        repository.save(user);
    }

    @Override
    public void updateLastInfoId(long chatId, int lastInfoId) {
        UserStatus user = repository.findByChatId(chatId);
        user.setLastInfoId(lastInfoId);
        repository.save(user);
    }

    @Override
    public List<UserStatus> getAllUsers() {
        return repository.findAll();
    }
}
