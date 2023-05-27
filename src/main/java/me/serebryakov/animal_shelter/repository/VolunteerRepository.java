package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Volunteer getVolunteerByChatId(long chatId);
}
