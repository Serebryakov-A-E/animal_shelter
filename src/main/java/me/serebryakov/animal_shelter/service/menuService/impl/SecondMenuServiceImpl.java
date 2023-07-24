package me.serebryakov.animal_shelter.service.menuService.impl;

import me.serebryakov.animal_shelter.repository.menuRepository.SecondMenuRepository;
import me.serebryakov.animal_shelter.service.menuService.SecondMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecondMenuServiceImpl implements SecondMenuService {
    private final SecondMenuRepository repository;


    public SecondMenuServiceImpl(SecondMenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = new ArrayList<>();
        repository.findAll().forEach(SecondMenu -> items.add(SecondMenu.getItem()));
        return items;
    }

    @Override
    public List<String> getMenuItemsByShelterId(int id) {
        List<String> items = new ArrayList<>();
        repository.findAll().stream().filter(secondMenu -> secondMenu.getMainMenuId().getId() == id).forEach(secondMenu -> {
            items.add(secondMenu.getItem());
        });
        return items;
    }

    @Override
    public int getMenuIdByItem(String item) {
        return repository.getByItem(item).getId();
    }
}
