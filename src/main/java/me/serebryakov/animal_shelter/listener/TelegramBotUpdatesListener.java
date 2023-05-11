package me.serebryakov.animal_shelter.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendContact;
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
                /*
                Contact contact = message.contact();
                if (("/start").equals(message.text())) {
                    {
                        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("");
                        SendMessage sendMessage = new SendMessage(chatId, "sss").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                        KeyboardButton keyboardButton = new KeyboardButton("CONTACTS").requestContact(true);
                        replyKeyboardMarkup.addRow(keyboardButton);

                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                        if (!sendResponse.isOk()) {
                            logger.error("Error sending message: {}", sendResponse.description());
                        }
                    }
                } else if (message.text() == null && message.contact() != null) {
                    System.out.println(contact.phoneNumber());
                }


                //SendResponse sendResponse = telegramBot.execute(sendContact);


                 */
                SendMessage sendMessage = telegramKeyboard.getResponse(message);
                SendResponse sendResponse = telegramBot.execute(sendMessage);
                if (!sendResponse.isOk()) {
                    logger.error("Error sending message: {}", sendResponse.description());
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
