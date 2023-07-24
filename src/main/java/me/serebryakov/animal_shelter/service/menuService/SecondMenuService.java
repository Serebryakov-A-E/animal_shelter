package me.serebryakov.animal_shelter.service.menuService;

import java.util.List;

public interface SecondMenuService {
    List<String> getMenuItems();

    List<String> getMenuItemsByShelterId(int id);

    int getMenuIdByItem(String item);
}
