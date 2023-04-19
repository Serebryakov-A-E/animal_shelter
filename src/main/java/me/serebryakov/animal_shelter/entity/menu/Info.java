package me.serebryakov.animal_shelter.entity.menu;

import jakarta.persistence.*;

@Entity
@Table(name = "info")
public class Info {
    @Id
    private int id;

    @Column(name = "info")
    private String info;

    @ManyToOne
    @JoinColumn(name = "second_menu_id", referencedColumnName = "second_menu_id")
    private SecondMenu secondMenu;
}
