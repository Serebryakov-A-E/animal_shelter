package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportByChatIdAndDateAndShelterId(long chatId, LocalDate date, int shelterId);
    List<Report> findReportsByDate(LocalDate date);

    List<Report> findReportsByDateAndReportStatus(LocalDate date, ReportStatus status);

    Report findReportByChatIdAndDate(long chatId, LocalDate date);
}
