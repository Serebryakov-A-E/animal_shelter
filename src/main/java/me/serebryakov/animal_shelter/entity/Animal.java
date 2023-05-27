package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность животного
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Enumerated
    @Column(name = "animal_type")
    private AnimalType animalType;
}
