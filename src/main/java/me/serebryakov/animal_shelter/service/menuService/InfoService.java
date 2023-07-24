package me.serebryakov.animal_shelter.service.menuService;

import java.util.List;

public interface InfoService {
    public List<String> getMenuItems(int shelterId, int infoId);

    String getInfo(String item, int shelterId);
}
