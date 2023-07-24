package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Volunteer;

public interface VolunteerService {
    void save(Volunteer volunteer);

    Volunteer getByChatId(long chatId);

    void resetLastReportId();
}
