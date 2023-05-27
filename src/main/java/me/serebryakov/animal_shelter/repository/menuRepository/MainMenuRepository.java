package me.serebryakov.animal_shelter.repository.menuRepository;

import me.serebryakov.animal_shelter.entity.menu.MainMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainMenuRepository extends JpaRepository<MainMenu, Integer> {
    public MainMenu getByItem(String item);
}
