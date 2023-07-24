package me.serebryakov.animal_shelter.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Класс представляющий отчёт о состоянии животного, который отправляет пользователь
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "reports")
public class Report {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@Id
    @Column(name = "chat_id")
    private long chatId;

    //@Id
    @Column(name = "date")
    private LocalDate date;

    //@Id
    @Column(name = "shelter_id")
    private int shelterId;

    @Column(name = "report_time")
    private LocalTime time;

    @Column(name = "text")
    private String text;

    @Column(name = "file_id")
    private String fileId;

    @Enumerated
    @Column(name = "status")
    private ReportStatus reportStatus = ReportStatus.UNCHECKED;
}
