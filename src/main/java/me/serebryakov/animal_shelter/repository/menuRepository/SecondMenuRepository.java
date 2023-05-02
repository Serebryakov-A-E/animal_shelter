package me.serebryakov.animal_shelter.repository.menuRepository;

import me.serebryakov.animal_shelter.entity.menu.MainMenu;
import me.serebryakov.animal_shelter.entity.menu.SecondMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondMenuRepository extends JpaRepository<SecondMenu, Integer> {
    public SecondMenu getByItem(String item);
}
