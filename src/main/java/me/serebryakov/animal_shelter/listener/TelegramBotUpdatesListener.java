package me.serebryakov.animal_shelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import me.serebryakov.animal_shelter.keyboard.TelegramKeyboard;
import me.serebryakov.animal_shelter.service.AnimalService;
import me.serebryakov.animal_shelter.service.OwnerService;
import me.serebryakov.animal_shelter.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final ReportService reportService;
    private final AnimalService animalService;
    private final OwnerService ownerService;
    private final ExecutorService executorService;
    private final TelegramKeyboard telegramKeyboard;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ReportService reportService, AnimalService animalService, OwnerService ownerService, TelegramKeyboard telegramKeyboard) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
        this.animalService = animalService;
        this.ownerService = ownerService;
        this.executorService = Executors.newFixedThreadPool(10);
        this.telegramKeyboard = telegramKeyboard;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        executorService.submit(() -> {
            try {
                list.forEach(update -> {
                    logger.info("handles update: {}", update);
                    Message message = update.message();
                    SendMessage sendMessage = telegramKeyboard.getResponse(message);

                    /*
                    if (message.photo() != null) {
                        //вызываем этот метод, если пришло фото
                        sendMessage = telegramKeyboard.getReportResponse(message, telegramBot);
                    }

                     */

                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                    if (!sendResponse.isOk()) {
                        logger.error("Error sending message: {}", sendResponse.description());
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
