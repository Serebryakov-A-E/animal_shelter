package me.serebryakov.animal_shelter.entity.menu;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс для меню 3 уровня, также хранит информацию для пользователя
 */
@Entity
@Table(name = "info")
@Data
public class Info {
    @Id
    private int id;

    @Column(name = "info")
    private String item;

    @Column(name = "text")
    private String text;

    @Column(name = "shelter_id")
    private int shelterId;

    @ManyToOne
    @JoinColumn(name = "second_menu_id", referencedColumnName = "second_menu_id")
    private SecondMenu secondMenu;
}
