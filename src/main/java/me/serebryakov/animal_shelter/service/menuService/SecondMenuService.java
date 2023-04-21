package me.serebryakov.animal_shelter.service.menuService;

import me.serebryakov.animal_shelter.repository.menuRepository.SecondMenuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecondMenuService {
    private final SecondMenuRepository repository;


    public SecondMenuService(SecondMenuRepository repository) {
        this.repository = repository;
    }

    public List<String> getMenuItems() {
        List<String> items = new ArrayList<>();
        repository.findAll().forEach(SecondMenu -> items.add(SecondMenu.getItem()));
        return items;
    }

    public List<String> getMenuItemsByShelterId(int id) {
        List<String> items = new ArrayList<>();
        repository.findAll().stream().filter(secondMenu -> secondMenu.getMainMenuId().getId() == id).forEach(secondMenu -> {
            items.add(secondMenu.getItem());
        });
        return items;
    }

    public int getMenuIdByItem(String item) {
        return repository.getByItem(item).getId();
    }
}
