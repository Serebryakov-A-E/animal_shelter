package me.serebryakov.animal_shelter.service.impl;

import me.serebryakov.animal_shelter.entity.Volunteer;
import me.serebryakov.animal_shelter.repository.VolunteerRepository;
import me.serebryakov.animal_shelter.service.VolunteerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService {
    private final VolunteerRepository repository;

    public VolunteerServiceImpl(VolunteerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Volunteer volunteer) {
        repository.save(volunteer);
    }

    @Override
    public Volunteer getByChatId(long chatId) {
        return repository.getVolunteerByChatId(chatId);
    }
    public List<Long> getAllVolunteersChatIds() {
        List<Long> result = new ArrayList<>();
        List<Volunteer> list = repository.findAll();
        list.forEach(volunteer -> result.add(volunteer.getChatId()));
        return result;
    }

    @Override
    public void resetLastReportId() {
        repository.findAll().forEach(volunteer -> volunteer.setReportId(0));
    }
}
