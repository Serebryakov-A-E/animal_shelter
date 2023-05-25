package me.serebryakov.animal_shelter.service;

import me.serebryakov.animal_shelter.entity.Volunteer;
import me.serebryakov.animal_shelter.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VolunteerService {
    private final VolunteerRepository repository;

    public VolunteerService(VolunteerRepository repository) {
        this.repository = repository;
    }

    public void save(Volunteer volunteer) {
        repository.save(volunteer);
    }

    public Volunteer getByChatId(long chatId) {
        return repository.getVolunteerByChatId(chatId);
    }
    public List<Long> getAllVolunteersChatIds() {
        List<Long> result = new ArrayList<>();
        List<Volunteer> list = repository.findAll();
        list.forEach(volunteer -> result.add(volunteer.getChatId()));
        return result;
    }
}
