package me.serebryakov.animal_shelter.keyboard;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import me.serebryakov.animal_shelter.service.menuService.MainMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramKeyboard {
    private final MainMenuService mainMenuService;

    public TelegramKeyboard(MainMenuService mainMenuService) {
        this.mainMenuService = mainMenuService;
    }

    public ReplyKeyboardMarkup getReplyKeyboardMarkup() {

        List<String> items = mainMenuService.getMenuItems();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Вызвать волонтера");
        items.forEach(replyKeyboardMarkup::addRow);

        return replyKeyboardMarkup;
    }


}
