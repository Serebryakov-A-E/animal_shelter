package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;
import me.serebryakov.animal_shelter.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public void save(Report report) {
        repository.save(report);
    }

    public Report findByChatIdAndDateAndShelterId(long chatId, LocalDate date, int shelterId) {
        return repository.findReportByChatIdAndDateAndShelterId(chatId, date, shelterId);
    }

    public List<Report> findReportsByDate(LocalDate date) {
        return repository.findReportsByDate(date);
    }

    public List<Report> findReportsByDateAndStatus(LocalDate date, ReportStatus status) {
        return repository.findReportsByDateAndReportStatus(date, status);
    }

    public Report findByChatIdAndDate(long chatId, LocalDate date) {
        return repository.findReportByChatIdAndDate(chatId, date);
    }

    public Report getById(long id) {
        return repository.getReportById(id);
    }

    public List<Report> getReportsListByStatus(ReportStatus status) {
        return repository.findReportsByReportStatus(status);
    }
}
