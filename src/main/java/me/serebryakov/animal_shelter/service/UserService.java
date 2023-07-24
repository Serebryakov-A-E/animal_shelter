package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.UserStatus;

import java.util.List;

public interface UserService {
    void create(long chatId);

    void save(UserStatus user);

    UserStatus getUserByChatId(long chatId);

    void updateShelterId(long chatId, int shelterId);

    void updateReportStatus(long chatId, boolean status);

    void updateMenuLevel(long chatId, int menuLevel);

    void updateLastInfoId(long chatId, int lastInfoId);

    List<UserStatus> getAllUsers();
}
