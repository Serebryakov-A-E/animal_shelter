package me.serebryakov.animal_shelter.keyboard;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import me.serebryakov.animal_shelter.service.menuService.InfoService;
import me.serebryakov.animal_shelter.service.menuService.MainMenuService;
import me.serebryakov.animal_shelter.service.menuService.SecondMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramKeyboard {
    private final MainMenuService mainMenuService;
    private final SecondMenuService secondMenuService;
    private final InfoService infoService;

    public TelegramKeyboard(MainMenuService mainMenuService, SecondMenuService secondMenuService, InfoService infoService) {
        this.mainMenuService = mainMenuService;
        this.secondMenuService = secondMenuService;
        this.infoService = infoService;
    }

    public SendMessage getResponse(Long chatId, String text) {
        //todo update user menu level
        //todo класс юзера, репозиторий и сервис к нему

        //код апдейта пользователя
        {
            user.save(chatId);
        }

        //получаем id приюта из бд пользователей
        Integer shelterId = user.getShelterId();
        //получаем последний левел меню
        Integer lastMenuLevel = user.getMenuLevel();

        //если приют ещё не выбран, то предлагаем выбрать приют из главного меню
        if (lastMenuLevel == null) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getMainMenu());
            //сохраняем уровень меню
            //будет что-то типа userService.updateMenuLevel(user chatId, menuLevel);
            user.updateMenuLevel(1);
            //выходим из метода, отправляем сообщение
            return new SendMessage(chatId, "Привет, похоже, что ты первый раз сдесь. Выбери приют, который тебя интересует!")
                    .replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));

            //если уровень меню 1, то запоминаем какой приют выбрал юзер
        } else if (lastMenuLevel == 1) {
            //получаем Id приюта по тексту сообщения и обновляем его в бд юзера
            shelterId = mainMenuService.getMenuIdByItem(text);
            user.updateShelterId(shelterId);
            //Предлагаем выбрать пункт из меню второго уровня
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getSecondMenu(shelterId));
            //сохраняем уровнь меню
            user.updateMenuLevel(2);
            //отправляем сообщение и выходим из метода
            return new SendMessage(chatId, "Что тебя интересует?").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        } else if (lastMenuLevel == 2) {
            //получаем id выбранной информации и id выбранного приюта из бд
            shelterId = userService.getShelterId();
            int infoId = secondMenuService.getMenuIdByItem(text);
            //Вызываем меню из меню информации
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getInfoMenu(shelterId, infoId));
            //обновляем уровень меню
            user.updateMenuLevel(3);
            //отправляем менюшку и выходим из метода
            return new SendMessage(chatId, "Выбери нужный раздел информации.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        } else if (lastMenuLevel == 3) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("<- Назад");
            //получаем корректный shelter id
            shelterId = userService.getShelterId();
            //отправляем на шаг назад
            user.updateMenuLevel(2);
            return new SendMessage(chatId, getInfo(text, shelterId)).replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        }

    }

    private String[] getMainMenu() {
        List<String> list = mainMenuService.getMenuItems();

        return list.toArray(String[]::new);
    }

    private String[] getSecondMenu(int shelterId) {
        List<String> list = secondMenuService.getMenuItemsByShelterId(shelterId);

        return list.toArray(String[]::new);
    }

    private String[] getInfoMenu(int shelterId, int infoId) {
        List<String> list = infoService.getMenuItems(shelterId, infoId);
        return list.toArray(String[]::new);
    }

    private String getInfo(String item, int shelterId) {
        return infoService.getInfo(item, shelterId);
    }
}
