package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}
