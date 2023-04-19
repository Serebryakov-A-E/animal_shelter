package me.serebryakov.animal_shelter.service.menuService;

import me.serebryakov.animal_shelter.repository.menuRepository.MainMenuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainMenuService {
    private final MainMenuRepository repository;


    public MainMenuService(MainMenuRepository repository) {
        this.repository = repository;
    }

    public List<String> getMenuItems() {
        List<String> items = new ArrayList<>();
        repository.findAll().forEach(mainMenu -> items.add(mainMenu.getItem()));
        return items;
    }
}
