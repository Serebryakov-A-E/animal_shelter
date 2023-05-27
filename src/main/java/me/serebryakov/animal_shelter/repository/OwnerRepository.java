package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    public Owner findByChatId(long chatId);
}
