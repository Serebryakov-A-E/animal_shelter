package me.serebryakov.animal_shelter.service.menuService;

import java.util.List;

public interface MainMenuService {
    List<String> getMenuItems();

    int getMenuIdByItem(String item);
}
