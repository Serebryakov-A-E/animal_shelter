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
import me.serebryakov.animal_shelter.service.impl.*;
import me.serebryakov.animal_shelter.service.menuService.impl.InfoServiceImpl;
import me.serebryakov.animal_shelter.service.menuService.impl.MainMenuServiceImpl;
import me.serebryakov.animal_shelter.service.menuService.impl.SecondMenuServiceImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TelegramKeyboard {
    private final MainMenuServiceImpl mainMenuServiceImpl;
    private final SecondMenuServiceImpl secondMenuServiceImpl;
    private final InfoServiceImpl infoServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final OwnerServiceImpl ownerServiceImpl;
    private final AnimalServiceImpl animalServiceImpl;
    private final ReportServiceImpl reportServiceImpl;

    public TelegramKeyboard(MainMenuServiceImpl mainMenuServiceImpl, SecondMenuServiceImpl secondMenuServiceImpl, InfoServiceImpl infoServiceImpl, UserServiceImpl userServiceImpl, OwnerServiceImpl ownerServiceImpl, AnimalServiceImpl animalServiceImpl, ReportServiceImpl reportServiceImpl) {
        this.mainMenuServiceImpl = mainMenuServiceImpl;
        this.secondMenuServiceImpl = secondMenuServiceImpl;
        this.infoServiceImpl = infoServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.ownerServiceImpl = ownerServiceImpl;
        this.animalServiceImpl = animalServiceImpl;
        this.reportServiceImpl = reportServiceImpl;
    }

    public SendMessage getResponse(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();
        Contact contact = message.contact();

        //код создания пользователя, если такого ещё нет
        if (userServiceImpl.getUserByChatId(chatId) == null) {
            userServiceImpl.create(chatId);
            userServiceImpl.updateMenuLevel(chatId, 0);
            //если пользователь сдесь первый раз, то выкидываем метод с особым приветствием!
            return getZeroLevelMenu(chatId, true);
        }
        //Также вносим в базу овнера если его ещё нет
        if (ownerServiceImpl.getByChatId(chatId) == null) {
            ownerServiceImpl.create(chatId);
        }

        //если пользователь находится в состоянии отправки репорта
        if (userServiceImpl.getUserByChatId(chatId).getIsSendingReport()) {
            if ("<- Назад".equals(text)) {
                userServiceImpl.updateReportStatus(chatId, false);
                return new SendMessage(chatId, "Отправка отчёта отменена.");
            }
            int shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();
            Report report = reportServiceImpl.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);

            if (report == null) {
                report = new Report();
                report.setChatId(chatId);
                report.setDate(LocalDate.now());
                report.setTime(LocalTime.now());
                report.setShelterId(shelterId);
                reportServiceImpl.save(report);
            }
            return updateReportInfo(message, shelterId);
        }

        if (("Главное меню").equals(text)) {
            return getZeroLevelMenu(chatId, false);
        }
        if (("/start").equals(text)) {
            userServiceImpl.updateMenuLevel(chatId, 0);
        }
        //если пришло пустое сообщение, проверям пришли ли контакты пользователя
        if (message.text() == null && message.contact() != null) {
            return saveContacts(chatId, contact);
        }

        //создаем переменную для id приюта
        Integer shelterId;
        //получаем последний левел меню
        Integer lastMenuLevel = userServiceImpl.getUserByChatId(chatId).getLastMenuLevel();

        if (text.equals("<- Назад")) {
            lastMenuLevel = userServiceImpl.getUserByChatId(chatId).getLastMenuLevel() - 1;
            switch (lastMenuLevel) {
                case 1:
                    return getZeroLevelMenu(chatId, false);
                case 2:
                    shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getSecondMenu(shelterId));
                    //апаем уровень меню
                    userServiceImpl.updateMenuLevel(chatId, 2);
                    //отправляем сообщение и выходим из метода
                    return new SendMessage(chatId, "Что тебя интересует?").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                case 3:
                    //получаем id выбранной информации и id выбранного приюта из бд
                    shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();
                    int infoId = userServiceImpl.getUserByChatId(chatId).getLastInfoId();
                    //Вызываем меню из меню информации
                    replyKeyboardMarkup = new ReplyKeyboardMarkup(getInfoMenu(shelterId, infoId));
                    //апаем уровень меню
                    userServiceImpl.updateMenuLevel(chatId, 3);
                    //отправляем менюшку и выходим из метода
                    return new SendMessage(chatId, "Выбери нужный раздел информации.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                default:
                    return new SendMessage(chatId, "ERROR!");
            }
        } else if (text.equals("Отправить отчёт")) {
            //создаем кнопку главного меню
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");
            //получаем ид приюта
            shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();

            //проверяем есть ли в базе контактные данные пользователя
            if (ownerServiceImpl.getByChatId(chatId).getPhoneNumber().isEmpty()) {
                userServiceImpl.updateReportStatus(chatId, false);
                //кнопка контактных данных
                KeyboardButton keyboardButton = new KeyboardButton("Оставить контактные данные").requestContact(true);
                replyKeyboardMarkup.addRow(keyboardButton);
                return new SendMessage(chatId, "Чтобы отправлять отчёт поделитесь контактными данными.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
            }
            //проверяем есть ли у пользователя какое-либо животное
            List<Animal> animals;
            if (shelterId == 1) {
                animals = animalServiceImpl.findAnimalsByOwnerAndAnimalType(ownerServiceImpl.getByChatId(chatId), AnimalType.CAT);
            } else if (shelterId == 2) {
                animals = animalServiceImpl.findAnimalsByOwnerAndAnimalType(ownerServiceImpl.getByChatId(chatId), AnimalType.DOG);
            } else {
                return new SendMessage(chatId, "ERROR");
            }
            if (animals.size() == 0) {
                return new SendMessage(chatId, "У вас нет животных.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
            }

            //проверяем создан ли отчёт
            Report report = reportServiceImpl.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);
            if (report != null) {
                //проверяем есть ли фото и текст
                if (report.getFileId() != null && report.getText() != null) {
                    //возвращает ответ, что отчёт сегодня уже был отправлен и его проверяет волонтер
                    //также выводим пользователя из состояния отправки отчёта
                    userServiceImpl.updateReportStatus(chatId, false);
                    return new SendMessage(chatId, "Вы уже отправляли отчёт сегодня.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                }
            }
            userServiceImpl.updateReportStatus(chatId, true);
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
     * @param chatId
     * @return
     */
    private SendMessage getZeroLevelMenu(long chatId, boolean isFirstTime) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getMainMenu());
        //сохраняем уровень меню
        userServiceImpl.updateMenuLevel(chatId, 1);
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
        int shelterId = mainMenuServiceImpl.getMenuIdByItem(text);
        userServiceImpl.updateShelterId(chatId, shelterId);
        //Предлагаем выбрать пункт из меню второго уровня
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getSecondMenu(shelterId));
        //сохраняем уровнь меню

        //кнопка контактных данных
        KeyboardButton keyboardButton = new KeyboardButton("Оставить контактные данные").requestContact(true);
        replyKeyboardMarkup.addRow(keyboardButton);

        userServiceImpl.updateMenuLevel(chatId, 2);
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
        int shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();
        int infoId = secondMenuServiceImpl.getMenuIdByItem(text);
        //добавляем юзеру послений выбраный ид информации, для работы функции назад
        userServiceImpl.updateLastInfoId(chatId, infoId);
        //Вызываем меню из меню информации
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(getInfoMenu(shelterId, infoId));
        //обновляем уровень меню
        userServiceImpl.updateMenuLevel(chatId, 3);

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
        int shelterId = userServiceImpl.getUserByChatId(chatId).getShelterId();
        //отправляем на шаг назад
        userServiceImpl.updateMenuLevel(chatId, 3);
        return new SendMessage(chatId, getInfo(text, shelterId)).replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
    }

    /**
     * Метод сохраняет контакты пользователя в бд
     * @param chatId чатИд пользователя
     * @param contact объект содержащий его контакты
     * @return возвращает сообщение о том, что контакты сохранены
     */
    private SendMessage saveContacts(long chatId, Contact contact) {
        ownerServiceImpl.saveContacts(chatId, contact);
        return new SendMessage(chatId, "Контактные данные успешно сохранены!");
    }


    /**
     * @return Метод возвращает массив строк, содержащий пункты главного меню. Выбор приюта.
     */
    private String[] getMainMenu() {
        List<String> list = mainMenuServiceImpl.getMenuItems();
        return list.toArray(String[]::new);
    }

    /**
     * @return Метод возвращает массив строк, содержащий пункты подменю.
     */
    private String[] getSecondMenu(int shelterId) {
        List<String> list = secondMenuServiceImpl.getMenuItemsByShelterId(shelterId);
        list.add("Отправить отчёт");
        list.add("<- Назад");
        return list.toArray(String[]::new);
    }

    /**
     * @param shelterId ид приюта
     * @param infoId ид конкретной информации
     * @return Метод возвращает меню в котором пользователь может выбрать интересующую его информацию.
     */
    private String[] getInfoMenu(int shelterId, int infoId) {
        List<String> list = infoServiceImpl.getMenuItems(shelterId, infoId);
        list.add("<- Назад");
        return list.toArray(String[]::new);
    }

    /**
     * @param item вовзращает тест из класса Info по item
     * @param shelterId ид приюта
     * @return Метод возвращает строку с информацией, выбранной пользователем.
     */
    private String getInfo(String item, int shelterId) {
        return infoServiceImpl.getInfo(item, shelterId);
    }

    /**
     * Метод принимает и обновляет информацию в отчёте
     * @param message новое сообщение для отчёта
     * @param shelterId id приюта
     * @return сообщение с состоянием отчёта
     */
    private SendMessage updateReportInfo(Message message, int shelterId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");

        long chatId = message.chat().id();
        Report report = reportServiceImpl.findByChatIdAndDateAndShelterId(chatId, LocalDate.now(), shelterId);
        if (message.text() != null) {
            //проверяем есть ли уже текст
            if (report.getText() != null) {
                report.setText(message.text());
                reportServiceImpl.save(report);
                return new SendMessage(chatId, "Сообщение отчёта обновлено.");
            } else {
                report.setText(message.text());
                reportServiceImpl.save(report);
                if (report.getFileId() != null) {
                    userServiceImpl.updateReportStatus(chatId, false);
                    //todo реализовать отправку сообщения волонтеру
                    return new SendMessage(chatId, "Ваш отчёт принят и отправлен на проверку волонтеру!").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
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
                reportServiceImpl.save(report);
                return new SendMessage(chatId, "Фото отчёта обновлено.");
            } else {
                report.setFileId(fileId);
                reportServiceImpl.save(report);
                if (report.getText() != null) {
                    userServiceImpl.updateReportStatus(chatId, false);
                    return new SendMessage(chatId, "Ваш отчёт принят и отправлен на проверку волонтеру!").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
                }
                return new SendMessage(chatId, "Фото отчёта принято. Ожидаем текст отчёта.");
            }
        }
        userServiceImpl.updateReportStatus(chatId, false);
        return new SendMessage(chatId, "ERROR");
    }

    /**
     * Метод возвращает отчёт с описанием и фото, а также две кнопки для волонтера "Одобрить" и "Отклонить"
     * Метод синхронизирован, чтобы два волонтера не могли получить на проверку одинаковый отчёт
     * @param chatId chat id Волонтера
     * @param telegramBot
     * @param volunteerServiceImpl
     * @return Метод возвращает SendPhoto с описанием.
     */

    public synchronized SendPhoto getUncheckedReport(long chatId, TelegramBot telegramBot, VolunteerServiceImpl volunteerServiceImpl) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Одобрить", "Отклонить");
        List<Report> reports = reportServiceImpl.findReportsByDateAndStatus(LocalDate.now(), ReportStatus.UNCHECKED);
        if (reports.size() == 0) {
            return null;
        }
        Report report = reports.get(0);
        String reportText = report.getText();
        String fileId = report.getFileId();
        long reportId = report.getId();

        //устанавливаем волонтеру ид проверяемого отчёта
        Volunteer volunteer = volunteerServiceImpl.getByChatId(chatId);
        volunteer.setReportId(reportId);
        volunteerServiceImpl.save(volunteer);

        //устанавливаем статус "на проверке"
        report.setReportStatus(ReportStatus.CHECKING);
        reportServiceImpl.save(report);

        long ownerChatId = report.getChatId();
        Owner owner = ownerServiceImpl.getByChatId(ownerChatId);
        //получаем байты фотки
        byte[] image = getFile(fileId, telegramBot);

        SendPhoto sendPhoto = new SendPhoto(chatId, image);
        sendPhoto.caption(reportText + "\n" + "Контакты для связи с хозяином животного:" + "\n" + owner.getName() + "\n" + owner.getPhoneNumber());
        sendPhoto.replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));

        return sendPhoto;
    }

    /**
     * Метод возвращает сообщение со списком всех репортов определённого статуса
     * @param chatId chat id юзера
     * @param status статус репортов, которые нужно вернуть
     * @return возвращает сообщение со списком всех репортов определённого статуса
     */
    public SendMessage getReportList(long chatId, ReportStatus status) {
        StringBuilder sb = new StringBuilder();
        List<Report> reports = reportServiceImpl.getReportsListByStatus(status);
        if (reports.size() == 0) {
            return null;
        }

        for (Report report : reports) {
            Owner owner = ownerServiceImpl.getByChatId(report.getChatId());
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
        return new SendMessage(chatId, sb.toString()).replyMarkup(new ReplyKeyboardMarkup("Главное меню").resizeKeyboard(true));
    }

    /**
     * Метод для получения байтов изображения от телеграма
     * @param fileId id изображения
     * @param telegramBot
     * @return байты изображения
     */
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

    /**
     * Метод устанавливает новый статус для отчёта после проверки
     * @param chatId id волонтера
     * @param status новый статус
     * @param reportId ид отчёта
     * @return Сообщение о новом статусе отчёта или сообщение об ошибке.
     */
    public SendMessage setReportStatus(long chatId, String status, long reportId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Главное меню");

        Report report = reportServiceImpl.getById(reportId);

        //выкидываем, если вдруг такого отчёта нет
        if (report == null) {
            return new SendMessage(chatId, "Ошибка установки статуса отчёта. Отчёта с таким Id нет.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        }

        if (status.equals("Одобрить")) {
            report.setReportStatus(ReportStatus.APPROVED);
            reportServiceImpl.save(report);
            return new SendMessage(chatId, "Спасибо за проверку. Статус отчёта обновлен на \"Одобрен\"").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        } else {
            report.setReportStatus(ReportStatus.REJECTED);
            reportServiceImpl.save(report);
            return new SendMessage(chatId, "Спасибо за проверку. Статус отчёта обновлен на \"Отклонен\". Свяжитесь с хозяином животного.").replyMarkup(replyKeyboardMarkup.resizeKeyboard(true));
        }
    }

    /**
     * Метод сбрасывает данные о том, в каком меню нахоидтся пользователь. Выполняется при запуске приложения
     */
    public void resetUserData() {
        for (UserStatus user : userServiceImpl.getAllUsers()) {
            user.setLastMenuLevel(0);
            user.setIsSendingReport(false);
            user.setShelterId(0);
            user.setLastInfoId(0);
            userServiceImpl.save(user);
        }
    }
}
