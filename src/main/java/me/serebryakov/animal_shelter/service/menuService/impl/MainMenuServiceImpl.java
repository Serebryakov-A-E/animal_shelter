package me.serebryakov.animal_shelter.service.menuService.impl;

import me.serebryakov.animal_shelter.repository.menuRepository.MainMenuRepository;
import me.serebryakov.animal_shelter.service.menuService.MainMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainMenuServiceImpl implements MainMenuService {
    private final MainMenuRepository repository;


    public MainMenuServiceImpl(MainMenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = new ArrayList<>();
        repository.findAll().forEach(mainMenu -> items.add(mainMenu.getItem()));
        return items;
    }

    @Override
    public int getMenuIdByItem(String item) {
        return repository.getByItem(item).getId();
    }
}
