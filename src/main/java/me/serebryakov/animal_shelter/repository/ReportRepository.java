package me.serebryakov.animal_shelter.repository;

import me.serebryakov.animal_shelter.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
