package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Owner {
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
