package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByChatId(long chatId);
}
