package me.serebryakov.animal_shelter.service.impl;

import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;
import me.serebryakov.animal_shelter.repository.ReportRepository;
import me.serebryakov.animal_shelter.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository repository;

    public ReportServiceImpl(ReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Report report) {
        repository.save(report);
    }

    @Override
    public Report findByChatIdAndDateAndShelterId(long chatId, LocalDate date, int shelterId) {
        return repository.findReportByChatIdAndDateAndShelterId(chatId, date, shelterId);
    }

    @Override
    public List<Report> findReportsByDate(LocalDate date) {
        return repository.findReportsByDate(date);
    }

    @Override
    public List<Report> findReportsByDateAndStatus(LocalDate date, ReportStatus status) {
        return repository.findReportsByDateAndReportStatus(date, status);
    }

    @Override
    public Report findByChatIdAndDate(long chatId, LocalDate date) {
        return repository.findReportByChatIdAndDate(chatId, date);
    }

    @Override
    public Report getById(long id) {
        return repository.getReportById(id);
    }

    @Override
    public List<Report> getReportsListByStatus(ReportStatus status) {
        return repository.findReportsByReportStatus(status);
    }
}
