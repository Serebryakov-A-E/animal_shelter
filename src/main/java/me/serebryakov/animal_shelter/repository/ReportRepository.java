package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<Report, Long> {
    public Report findReportByChatIdAndDateAndShelterId(long chatId, LocalDate date, int shelterId);
}
