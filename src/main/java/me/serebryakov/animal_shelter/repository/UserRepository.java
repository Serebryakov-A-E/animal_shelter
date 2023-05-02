package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserStatus, Long> {
    public UserStatus findByChatId(long chatId);
}
