package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_info")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "last_menu_level")
    private int lastMenuLevel;

    @Column(name = "last_info_id")
    private int lastInfoId;

    @Column(name = "shelter_id")
    private int shelterId;
}
