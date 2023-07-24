package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    void save(Report report);

    Report findByChatIdAndDateAndShelterId(long chatId, LocalDate date, int shelterId);

    List<Report> findReportsByDate(LocalDate date);

    List<Report> findReportsByDateAndStatus(LocalDate date, ReportStatus status);

    Report findByChatIdAndDate(long chatId, LocalDate date);

    Report getById(long id);

    List<Report> getReportsListByStatus(ReportStatus status);
}
