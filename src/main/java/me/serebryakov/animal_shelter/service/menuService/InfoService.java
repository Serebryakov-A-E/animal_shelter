package me.serebryakov.animal_shelter.service.menuService;

import me.serebryakov.animal_shelter.repository.menuRepository.InfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoService {
    private final InfoRepository repository;

    public InfoService(InfoRepository repository) {
        this.repository = repository;
    }

    public List<String> getMenuItems(int shelterId, int infoId) {
        List<String> items = new ArrayList<>();
        repository.findAll().stream().filter(item -> item.getShelterId() == shelterId && item.getSecondMenu().getId() == infoId)
                .forEach(item -> items.add(item.getItem()));
        return items;
    }

    public String getInfo(String item, int shelterId) {
        return repository.getInfoByItemAndShelterId(item, shelterId).getText();
    }
}
