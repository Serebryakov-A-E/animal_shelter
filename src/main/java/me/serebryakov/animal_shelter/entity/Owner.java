package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
/**
 * Сущность хозяина животного или животных
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "name")
    private String name = "";

    @Column(name = "phone")
    private String phoneNumber = "";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Animal> animalList = new ArrayList<>();
}
