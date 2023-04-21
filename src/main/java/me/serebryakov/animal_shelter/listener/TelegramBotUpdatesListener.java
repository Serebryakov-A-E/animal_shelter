package me.serebryakov.animal_shelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import me.serebryakov.animal_shelter.entity.Report;
import me.serebryakov.animal_shelter.keyboard.TelegramKeyboard;
import me.serebryakov.animal_shelter.service.AnimalService;
import me.serebryakov.animal_shelter.service.OwnerService;
import me.serebryakov.animal_shelter.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final ReportService reportService;
    private final AnimalService animalService;
    private final OwnerService ownerService;

    private final TelegramKeyboard telegramKeyboard;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ReportService reportService, AnimalService animalService, OwnerService ownerService, TelegramKeyboard telegramKeyboard) {
        this.telegramBot = telegramBot;
        this.reportService = reportService;
        this.animalService = animalService;
        this.ownerService = ownerService;
        this.telegramKeyboard = telegramKeyboard;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        try {
            list.forEach(update -> {
                logger.info("handles update: {}", update);

                Message message = update.message();
                Long chatId = message.chat().id();
                String text = message.text();



                SendMessage sendMessage = telegramKeyboard.getResponse(chatId, text);

                SendResponse sendResponse = telegramBot.execute(sendMessage);
                if (!sendResponse.isOk()) {
                    logger.error("Error sending message: {}", sendResponse.description());
                }

                /*
                if ("/start".equals(text)) {
                    ReplyKeyboardMarkup replyKeyboardMarkup = telegramKeyboard.getReplyKeyboardMarkup(1);
                    SendMessage sendMessage = new SendMessage(chatId, "Привет. Это телеграмм бот приюта." +
                            "Выбери один из пунктов меню.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                    if (!sendResponse.isOk()) {
                        logger.error("Error sending message: {}", sendResponse.description());
                    }

                    String item = message.text();
                    ReplyKeyboardMarkup replyKeyboardMarkup1 = telegramKeyboard.getReplyKeyboardMarkup(2, item);
                    SendMessage sendMessage1 = new SendMessage(chatId, "Выбери пунк меню для своего приюта.")
                            .replyMarkup(replyKeyboardMarkup1.resizeKeyboard(true));
                    SendResponse sendResponse1 = telegramBot.execute(sendMessage1);

                    if (!sendResponse1.isOk()) {
                        logger.error("Error sending message: {}", sendResponse1.description());
                    }
                }
                */
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
