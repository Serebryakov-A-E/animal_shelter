package me.serebryakov.animal_shelter.service.menuService.impl;

import me.serebryakov.animal_shelter.repository.menuRepository.InfoRepository;
import me.serebryakov.animal_shelter.service.menuService.InfoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoServiceImpl implements InfoService {
    private final InfoRepository repository;

    public InfoServiceImpl(InfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> getMenuItems(int shelterId, int infoId) {
        List<String> items = new ArrayList<>();
        repository.findAll().stream().filter(item -> item.getShelterId() == shelterId && item.getSecondMenu().getId() == infoId)
                .forEach(item -> items.add(item.getItem()));
        return items;
    }

    @Override
    public String getInfo(String item, int shelterId) {
        return repository.getInfoByItemAndShelterId(item, shelterId).getText();
    }
}
