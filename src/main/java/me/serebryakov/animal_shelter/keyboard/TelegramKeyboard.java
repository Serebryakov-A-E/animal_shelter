package me.serebryakov.animal_shelter.keyboard;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetFileResponse;
import me.serebryakov.animal_shelter.entity.*;
import me.serebryakov.animal_shelter.entity.menu.ReportStatus;
import me.serebryakov.animal_shelter.service.*;
import me.serebryakov.animal_shelter.service.menuService.InfoService;
import me.serebryakov.animal_shelter.service.menuService.MainMenuService;
import me.serebryakov.animal_shelter.service.menuService.SecondMenuService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TelegramKeyboard {
    private final MainMenuService mainMenuService;
    private final SecondMenuService secondMenuService;
    private final InfoService infoService;
    private final UserService userService;
    private final OwnerService ownerService;
    private final AnimalService animalService;
    private final ReportService reportService;

    public TelegramKeyboard(MainMenuService mainMenuService, SecondMenuService secondMenuService, InfoService infoService, UserService userService, OwnerService ownerService, AnimalService animalService, ReportService reportService) {
        this.mainMenuService = mainMenuService;
        this.secondMenuService = secondMenuService;
        this.infoService = infoService;
        this.userService = userService;
        this.ownerService = ownerService;
        this.animalService = animalService;
        this.reportService = reportService;
    }

    public SendMessage getResponse(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();
        Contact contact = message.contact();

        //код создания пользователя, если такого ещё нет
        if (userService.getUserByChatId(chatId) == null) {
            userService.create(chatId);
            userService.updateMenuLevel(chatId, 0);
            //если пользователь сдесь первый раз, то выкидываем метод с особым приветствием!
            return getZeroLevelMenu(chatId, true);
        }
        //Также вносим в базу овнера если его ещё нет
        if (ownerService.getByChatId(chatId) == null) {
            ownerService.create(chatId);
        }


        //если пользователь находится в состоянии отправки репорта
        if (userService.getUserByChatId(chatId).getIsSendingReport()) {
            int shelterId = userService.getUserByChatId(chatId).getShelterId();
            Report report = reportService.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);

            if (report == null) {
                report = new Report();
                report.setChatId(chatId);
                report.setDate(LocalDate.now());
                report.setTime(LocalTime.now());
                report.setShelterId(shelterId);
                reportService.save(report);
            }
            return updateReportInfo(message, shelterId);
        }

        if (("Главное меню").equals(text)) {
            return getZeroLevelMenu(chatId, false);
        }
        if (("/start").equals(text)) {
            userService.updateMenuLevel(chatId, 0);
        }
        //если пришло пустое сообщение, проверям пришли ли контакты пользователя
        if (message.text() == null && message.contact() != null) {
            return saveContacts(chatId, contact);
        }

        //создаем переменную для id приюта
        Integer shelterId;
        //получаем последний левел меню
        Integer lastMenuLevel = userService.getUserByChatId(chatId).getLastMenuLevel();

        if (text.equals("<- Назад")) {
            lastMenuLevel = userService.getUserByChatId(chatId).getLastMenuLevel() - 1;
            switch (lastMenuLevel) {
                case 1:
                    return getZeroLevelMenu(chatId, false);
                case 2:
                    shelterId = userService.getUserByChatId(chatId).getShelterId();
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getSecondMenu(shelterId));
                    //апаем уровень меню
                    userService.updateMenuLevel(chatId, 2);
                    //отправляем сообщение и выходим из метода
                    return new SendMessage(chatId, "Что тебя интересует?").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                case 3:
                    //получаем id выбранной информации и id выбранного приюта из бд
                    shelterId = userService.getUserByChatId(chatId).getShelterId();
                    int infoId = userService.getUserByChatId(chatId).getLastInfoId();
                    //Вызываем меню из меню информации
                    replyKeyboardMarkup = new ReplyKeyboardMarkup(getInfoMenu(shelterId, infoId));
                    //апаем уровень меню
                    userService.updateMenuLevel(chatId, 3);
                    //отправляем менюшку и выходим из метода
                    return new SendMessage(chatId, "Выбери нужный раздел информации.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                default:
                    return new SendMessage(chatId, "ERROR!");
            }
        } else if (text.equals("Отправить отчёт")) {
            //создаем кнопку главного меню
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");
            //получаем ид приюта
            shelterId = userService.getUserByChatId(chatId).getShelterId();

            //проверяем есть ли у пользователя какое-либо животное
            List<Animal> animals;
            if (shelterId == 1) {
                animals = animalService.findAnimalsByOwnerAndAnimalType(ownerService.getByChatId(chatId), AnimalType.CAT);
            } else if (shelterId == 2) {
                animals = animalService.findAnimalsByOwnerAndAnimalType(ownerService.getByChatId(chatId), AnimalType.DOG);
            } else {
                return new SendMessage(chatId, "ERROR");
            }
            if (animals.size() == 0) {
                return new SendMessage(chatId, "У вас нет животных.").replyMarkup(replyKeyboardMarkup);
            }

            //проверяем создан ли отчёт
            Report report = reportService.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);
            if (report != null) {
                //проверяем есть ли фото и текст
                if (report.getFileId() != null && report.getText() != null) {
                    //возвращает ответ, что отчёт сегодня уже был отправлен и его проверяет волонтер
                    //также выводим пользователя из состояния отправки отчёта
                    userService.updateReportStatus(chatId, false);
                    return new SendMessage(chatId, "Вы уже отправляли отчёт сегодня.").replyMarkup(replyKeyboardMarkup);
                }
            }
            userService.updateReportStatus(chatId, true);
            return new SendMessage(chatId, """
                    Следующим сообщением отправьте отчёт по форме:

                    Кличка животного:
                    Самочувствие:
                    Другая информация:
                    Фотография питомца""");
        }

        //если приют ещё не выбран, то предлагаем выбрать приют из главного меню
        if (lastMenuLevel == 0) {
            return getZeroLevelMenu(chatId, false);
        } else if (lastMenuLevel == 1) {
            return getFirstLevelMenu(chatId, text);
        } else if (lastMenuLevel == 2) {
            return getSecondLevelMenu(chatId, text);
        } else if (lastMenuLevel == 3) {
            return getThirdLevelMenu(chatId, text);
        } else {
            return new SendMessage(chatId, "ERROR!");
        }
    }

    /**
     * Метод возвращает сообщение с меню из бд, для главного меню, где предлагается выбрать один из приютов.
     *
     * @param chatId
     * @return
     */
    private SendMessage getZeroLevelMenu(long chatId, boolean isFirstTime) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getMainMenu());
        //сохраняем уровень меню
        userService.updateMenuLevel(chatId, 1);
        //выходим из метода, отправляем сообщение
        if (isFirstTime) {
            return new SendMessage(chatId, "Привет, похоже, что ты первый раз здесь. Выбери приют который тебя интересует!")
                    .replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        }
        return new SendMessage(chatId, "Выбери приют который тебя интересует!")
                .replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
    }

    /**
     * Метод возвращает сообщение с меню из бд, для первого уровня меню.
     *
     * @param chatId
     * @param text
     * @return
     */
    private SendMessage getFirstLevelMenu(long chatId, String text) {
        //получаем Id приюта по тексту сообщения и обновляем его в бд юзера
        int shelterId = mainMenuService.getMenuIdByItem(text);
        userService.updateShelterId(chatId, shelterId);
        //Предлагаем выбрать пункт из меню второго уровня
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getSecondMenu(shelterId));
        //сохраняем уровнь меню

        //кнопка контактных данных
        KeyboardButton keyboardButton = new KeyboardButton("Оставить контактные данные").requestContact(true);
        replyKeyboardMarkup.addRow(keyboardButton);

        userService.updateMenuLevel(chatId, 2);
        //отправляем сообщение и выходим из метода
        return new SendMessage(chatId, "Что тебя интересует?").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
    }

    /**
     * Метод возвращает сообщение с меню из бд, для второго уровня меню.
     *
     * @param chatId
     * @param text
     * @return
     */
    private SendMessage getSecondLevelMenu(long chatId, String text) {
        //получаем id выбранной информации и id выбранного приюта из бд
        int shelterId = userService.getUserByChatId(chatId).getShelterId();
        int infoId = secondMenuService.getMenuIdByItem(text);
        //добавляем юзеру послений выбраный ид информации, для работы функции назад
        userService.updateLastInfoId(chatId, infoId);
        //Вызываем меню из меню информации
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getInfoMenu(shelterId, infoId));
        //обновляем уровень меню
        userService.updateMenuLevel(chatId, 3);

        //отправляем менюшку и выходим из метода
        return new SendMessage(chatId, "Выбери нужный раздел информации.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
    }

    /**
     * Метод возвращает сообщение с информацией для пользователя.
     *
     * @param chatId
     * @param text
     * @return
     */
    private SendMessage getThirdLevelMenu(long chatId, String text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("<- Назад");
        //получаем корректный shelter id
        int shelterId = userService.getUserByChatId(chatId).getShelterId();
        //отправляем на шаг назад
        userService.updateMenuLevel(chatId, 3);
        return new SendMessage(chatId, getInfo(text, shelterId)).replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
    }

    private SendMessage saveContacts(long chatId, Contact contact) {
        ownerService.saveContacts(chatId, contact);
        return new SendMessage(chatId, "Контактные данные успешно сохранены!");
    }


    /**
     * Метод возвращает массив строк, содержащий пункты главного меню. Выбор приюта.
     *
     * @return
     */
    private String[] getMainMenu() {
        List<String> list = mainMenuService.getMenuItems();
        return list.toArray(String[]::new);
    }

    /**
     * Метод возвращает массив строк, содержащий пункты подменю.
     *
     * @return
     */
    private String[] getSecondMenu(int shelterId) {
        List<String> list = secondMenuService.getMenuItemsByShelterId(shelterId);
        list.add("Отправить отчёт");
        list.add("<- Назад");
        return list.toArray(String[]::new);
    }

    /**
     * Метод возвращает меню в котором пользователь может выбрать интересующую его информацию.
     *
     * @param shelterId
     * @param infoId
     * @return
     */
    private String[] getInfoMenu(int shelterId, int infoId) {
        List<String> list = infoService.getMenuItems(shelterId, infoId);
        list.add("<- Назад");
        return list.toArray(String[]::new);
    }

    /**
     * Метод возвращает строку с информацией, выбранной пользователем.
     *
     * @param item
     * @param shelterId
     * @return
     */
    private String getInfo(String item, int shelterId) {
        return infoService.getInfo(item, shelterId);
    }

    private SendMessage updateReportInfo(Message message, int shelterId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");

        long chatId = message.chat().id();
        Report report = reportService.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);
        if (message.text() != null) {
            //проверяем есть ли уже текст
            if (report.getText() != null) {
                report.setText(message.text());
                reportService.save(report);
                return new SendMessage(chatId, "Сообщение отчёта обновлено.");
            } else {
                report.setText(message.text());
                reportService.save(report);
                if (report.getFileId() != null) {
                    userService.updateReportStatus(chatId, false);
                    //todo реализовать отправку сообщения волонтеру
                    return new SendMessage(chatId, "Ваш отчёт принят и отправлен на проверку волонтеру!").replyMarkup(replyKeyboardMarkup);
                }
                return new SendMessage(chatId, "Сообщение отчёта принято. Ожидаем фото отчёта.");
            }
        }
        if (message.photo() != null) {

            PhotoSize photoSize = message.photo()[message.photo().length - 1];
            //Сохраняем только fileId, чтобы не хранить фотографии локально. Они храняться на серверах телеграма.
            String fileId = photoSize.fileId();

            //проверяем есть ли уже фото
            if (report.getFileId() != null) {
                report.setFileId(fileId);
                reportService.save(report);
                return new SendMessage(chatId, "Фото отчёта обновлено.");
            } else {
                report.setFileId(fileId);
                reportService.save(report);
                if (report.getText() != null) {
                    userService.updateReportStatus(chatId, false);
                    return new SendMessage(chatId, "Ваш отчёт принят и отправлен на проверку волонтеру!").replyMarkup(replyKeyboardMarkup);
                }
                return new SendMessage(chatId, "Фото отчёта принято. Ожидаем текст отчёта.");
            }
        }
        userService.updateReportStatus(chatId, false);
        return new SendMessage(chatId, "ERROR");
    }

    //метод синхронизирован, чтобы два волонтера не могли получить на проверку одинаковый отчёт
    public synchronized SendPhoto getUncheckedReports(long chatId, TelegramBot telegramBot, VolunteerService volunteerService) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Одобрить", "Отклонить");
        List<Report> reports = reportService.findReportsByDateAndStatus(LocalDate.now(), ReportStatus.UNCHECKED);
        if (reports.size() == 0) {
            return null;
        }
        Report report = reports.get(0);
        String reportText = report.getText();
        String fileId = report.getFileId();
        long reportId = report.getId();

        //устанавливаем волонтеру ид проверяемого отчёта
        Volunteer volunteer = volunteerService.getByChatId(chatId);
        volunteer.setReportId(reportId);
        volunteerService.save(volunteer);

        //устанавливаем статус "на проверке"
        report.setReportStatus(ReportStatus.CHECKING);
        reportService.save(report);

        long ownerChatId = report.getChatId();
        Owner owner = ownerService.getByChatId(ownerChatId);
        //получаем байты фотки
        byte[] image = getFile(fileId, telegramBot);

        SendPhoto sendPhoto = new SendPhoto(chatId, image);
        sendPhoto.caption(reportText + "\n" + "Контакты для связи с хозяином животного:" + "\n" + owner.getName() + "\n" + owner.getPhoneNumber());
        sendPhoto.replyMarkup(replyKeyboardMarkup);

        return sendPhoto;
    }

    public SendMessage getReportList(long chatId, ReportStatus status) {
        StringBuilder sb = new StringBuilder();
        List<Report> reports = reportService.getReportsListByStatus(status);
        if (reports.size() == 0) {
            return null;
        }

        for (Report report : reports) {
            Owner owner = ownerService.getByChatId(report.getChatId());
            sb.append("id отчёта - ").append(report.getId()).append("\nДата отчёта: ").append(report.getDate()).append("\nТекст отчёта: ").append(report.getText());
            String shelterInfo;
            if (report.getShelterId() == 1) {
                shelterInfo = "Кошачий";
            } else if (report.getShelterId() == 2) {
                shelterInfo = "Собачий";
            } else {
                shelterInfo = "Нет информации о приюте";
            }
            sb.append("\nИнформация о приюте: ").append(shelterInfo);
            sb.append("\nКонтакты для связи: ").append(owner.getPhoneNumber()).append(" ").append(owner.getName());
            sb.append("\n");
            sb.append("\n");
        }
        return new SendMessage(chatId, sb.toString()).replyMarkup(new ReplyKeyboardMarkup("Главное меню"));
    }

    private byte[] getFile(String fileId, TelegramBot telegramBot) {
        GetFileResponse getFileResponse = telegramBot.execute(new GetFile(fileId));
        if (getFileResponse.isOk()) {
            try {
                return telegramBot.getFileContent(getFileResponse.file());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    //todo поменять. на просто менять статус по репорт ид
    public SendMessage setReportStatus(long chatId, String status, long reportId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");

        Report report = reportService.getById(reportId);

        //выкидываем, если вдруг такого отчёта нет
        if (report == null) {
            return new SendMessage(chatId, "Ошибка установки статуса отчёта. Отчёта с таким Id нет.").replyMarkup(replyKeyboardMarkup);
        }

        if (status.equals("Одобрить")) {
            report.setReportStatus(ReportStatus.APPROVED);
            reportService.save(report);
            return new SendMessage(chatId, "Спасибо за проверку. Статус отчёта обновлен на \"Одобрен\"").replyMarkup(replyKeyboardMarkup);
        } else {
            report.setReportStatus(ReportStatus.REJECTED);
            reportService.save(report);
            return new SendMessage(chatId, "Спасибо за проверку. Статус отчёта обновлен на \"Отклонен\". Свяжитесь с хозяином животного.").replyMarkup(replyKeyboardMarkup);
        }
    }

    /**
     * Метод сбрасывает данные о том, в каком меню нахоидтся пользователь. Выполняется при запуске приложения
     */
    public void resetUserData() {
        for (UserStatus user : userService.getAllUsers()) {
            user.setLastMenuLevel(0);
            user.setIsSendingReport(false);
            user.setShelterId(0);
            user.setLastInfoId(0);
        }
    }
}
