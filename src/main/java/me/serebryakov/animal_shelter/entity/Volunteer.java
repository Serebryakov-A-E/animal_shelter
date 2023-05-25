package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {
    @Id
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "last_report_chat_id")
    private long reportChatId = 0L;
}
