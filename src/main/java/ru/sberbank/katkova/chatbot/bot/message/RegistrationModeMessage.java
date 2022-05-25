/**
 * Обработка сообщений от пользователя в режиме регистрации пользователя.
 * processUpdate() - в зависимости от типа сообщения, обрабатывает полученное сообщение и формирует ответ в sendMessage для отправки пользователю.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.scheduler.Scheduler;
import ru.sberbank.katkova.chatbot.scheduler.SchedulerRepository;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;
import ru.sberbank.katkova.chatbot.user.UserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationModeMessage extends Message {
    private static final Long WEEKLY_DELAY = 604800000L; // 1 неделя
    private static final Long MONTHLY_DELAY = 2419200000L; // 1 месяц
    private static final Long MONTHLY3_DELAY = 7257600000L; // 3 месяца
    private static final String ENTER_FIRST_NAME = "Введите ваше имя";
    private static final String ENTER_LAST_NAME = "Введите вашу фамилию";
    private static final String ENTER_EMPLOYMENT_DATE = "Введите дату трудоустройства в формате ДД.ММ.ГГГГ";
    private static final String ALREADY_REGISTERED = "Вы уже зарегистрированы!";
    private static final String NOW_REGISTERED = "Теперь вы зарегистрированы!";
    private static final String TRAINING_REMINDER = "Напоминаем, что вам необходимо повторно пройти инструктаж!";
    private static final String FIRST_NAME_IS = "Вы ввели имя: ";
    private static final String LAST_NAME_IS = "Вы ввели фамилию: ";
    private static final String DATE_IS = "Вы ввели дату: ";
    private static final String IF_INCORRECT = ". Если вы ошиблись, введите /clear, чтобы начать регистрацию заново.\n";
    private static final String IF_CORRECT = "Если все верно, нажмите:";
    private static final String DATE_FORMAT_IS_INCORRECT = "Введена дата в неверном формате. Попробуйте ввести дату в формате ДД.ММ.ГГГГ";
    private static final String FINISH_REGISTRATION = "Закончить регистрацию";

    @Override
    public SendMessage processUpdate(Update update, User user) {

        SendMessage sendMessage = new SendMessage();
        UserRepository connectionU = new UserRepository();
        SchedulerRepository connectionS = new SchedulerRepository();

        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("register")) {
                if (user.getFirstName() == null) {
                    sendMessage.setText(ENTER_FIRST_NAME);
                } else if (user.getFirstName() != null && user.getLastName() == null) {
                    sendMessage.setText(ENTER_LAST_NAME);
                } else if (user.getFirstName() != null && user.getLastName() != null && user.getEmploymentDate() == null) {
                    sendMessage.setText(ENTER_EMPLOYMENT_DATE);
                } else sendMessage.setText(ALREADY_REGISTERED);
            } else if (update.getCallbackQuery().getData().equals("finishRegistration")) {
                java.sql.Date sqlDate = new java.sql.Date(user.getEmploymentDate().getTime());
                connectionU.updateUser(user.getChatId(), user.getFirstName(), user.getLastName(), sqlDate);
                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
                InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText("Раз в неделю").setCallbackData("weekly");
                inlineKeyboardButton2.setText("Раз в месяц").setCallbackData("monthly");
                inlineKeyboardButton3.setText("Раз в три месяца").setCallbackData("monthly3");
                inlineKeyboardButton4.setText("Никогда").setCallbackData("never");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list1 = new ArrayList<>();
                list1.add(inlineKeyboardButton1);
                list1.add(inlineKeyboardButton2);
                List<InlineKeyboardButton> list2 = new ArrayList<>();
                list2.add(inlineKeyboardButton3);
                list2.add(inlineKeyboardButton4);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list1);
                rowList.add(list2);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText(NOW_REGISTERED + "Как часто вы хотите получать напоминания о прохождении инструктажа?").setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            } else if (update.getCallbackQuery().getData().equals("weekly")) {
                user.setMode(Mode.NONE);
                String scheduleText = TRAINING_REMINDER;
                user.addTask(new Scheduler(user.getChatId(), scheduleText, WEEKLY_DELAY, WEEKLY_DELAY));
                connectionS.createNewSchedule(user.getChatId(), scheduleText, WEEKLY_DELAY, WEEKLY_DELAY);
                sendMessage.setText("Напоминание добавлено!");
            } else if (update.getCallbackQuery().getData().equals("monthly")) {
                user.setMode(Mode.NONE);
                String scheduleText = TRAINING_REMINDER;
                user.addTask(new Scheduler(user.getChatId(), scheduleText, MONTHLY_DELAY, MONTHLY_DELAY));
                connectionS.createNewSchedule(user.getChatId(), scheduleText, MONTHLY_DELAY, MONTHLY_DELAY);
                sendMessage.setText("Напоминание добавлено!");
            } else if (update.getCallbackQuery().getData().equals("monthly3")) {
                user.setMode(Mode.NONE);
                String scheduleText = TRAINING_REMINDER;
                user.addTask(new Scheduler(user.getChatId(), scheduleText, MONTHLY3_DELAY, MONTHLY3_DELAY));
                connectionS.createNewSchedule(user.getChatId(), scheduleText, MONTHLY3_DELAY, MONTHLY3_DELAY);
                sendMessage.setText("Напоминание добавлено!");
            } else if (update.getCallbackQuery().getData().equals("never")) {
                user.setMode(Mode.NONE);
                sendMessage.setText("Напоминание не создано!");
            }
        } else if (update.getMessage().hasText() && !update.getMessage().isCommand()) {
            if (user.getFirstName() == null) {
                user.setFirstName(update.getMessage().getText());
                sendMessage.setText(FIRST_NAME_IS + user.getFirstName() + IF_INCORRECT + ENTER_LAST_NAME);
            } else if (user.getFirstName() != null && user.getLastName() == null) {
                user.setLastName(update.getMessage().getText());
                sendMessage.setText(LAST_NAME_IS + user.getFirstName() + IF_INCORRECT + ENTER_EMPLOYMENT_DATE);
            } else if (user.getFirstName() != null && user.getLastName() != null && user.getEmploymentDate() == null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = dateFormat.parse(update.getMessage().getText());
                    user.setEmploymentDate(date);
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText(FINISH_REGISTRATION).setCallbackData("finishRegistration");
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> list = new ArrayList<>();
                    list.add(inlineKeyboardButton);
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(list);
                    inlineKeyboardMarkup.setKeyboard(rowList);
                    String dateFormatted = dateFormat.format(user.getEmploymentDate());
                    sendMessage.setText(DATE_IS + dateFormatted + IF_INCORRECT + IF_CORRECT).setReplyMarkup(inlineKeyboardMarkup);
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                } catch (java.text.ParseException e) {
                    sendMessage.setText(DATE_FORMAT_IS_INCORRECT);
                    return sendMessage;
                }
            } else sendMessage.setText(WELCOME_TEXT);
        } else if (update.getMessage().hasText() && update.getMessage().getText().equals("/clear")) {
            user.setFirstName(null);
            user.setLastName(null);
            user.setEmploymentDate(null);
            sendMessage.setText(ENTER_FIRST_NAME);
        } else {
            user.setMode(Mode.NONE);
            sendMessage.setText(WELCOME_TEXT);
        }
        return sendMessage;
    }

    @Override
    public SendMessage processUpdate(Update update, User user, List<Question> questionPool) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setText(WELCOME_TEXT);
    }

    @Override
    public SendMessage processUpdate(Update update, List<User> userList, User user) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setText(WELCOME_TEXT);
    }
}
