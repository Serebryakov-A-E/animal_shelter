package me.serebryakov.animal_shelter.repository.menuRepository;

import me.serebryakov.animal_shelter.entity.menu.Info;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, Integer> {
    public Info getInfoByItemAndShelterId(String item, int shelterId);
}
