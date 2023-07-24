package me.serebryakov.animal_shelter.entity.menu;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс хранит пункты главного меню
 */
@Entity
@Data
public class MainMenu {
    @Id
    @Column(name = "main_menu_id")
    private int id;

    @Column(name = "menu_item")
    private String item;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mainMenuId")
    private Set<SecondMenu> secondMenuSet = new HashSet<>();
}
