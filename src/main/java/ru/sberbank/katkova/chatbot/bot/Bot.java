/**
 * Основной класс телеграм-бота.
 * Объект бот создается с двумя параметрами: список пользователей бота, и список вопросов для инструктажа.
 * onUpdateReceived() - метод реализует получение и обработку сообщения от пользователя, а также отправку ответа.
 * Переопределенные методы getBotUsername() и getBotToken() возвращают имя и токен телеграм-бота.
 */

package ru.sberbank.katkova.chatbot.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.sberbank.katkova.chatbot.bot.message.*;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;
import ru.sberbank.katkova.chatbot.user.UserRepository;

import java.text.ParseException;
import java.util.List;
import java.util.Date;

public class Bot extends TelegramLongPollingBot {

    public static final String BOT_USER_NAME = "YetAnotherFireSafetyBot";
    public static final String BOT_TOKEN = "1686156581:AAEU0aYOSTkPcmFZFkp6eVI-7lMGeZz6cDY";

    private List<User> userList;
    private List<Question> questionPool;
    UserRepository connectionU = new UserRepository();


    public Bot() {
    }

    public Bot(List<Question> questionPool, List<User> userList) {
        this.questionPool = questionPool;
        this.userList = userList;
    }

    public void onUpdateReceived(Update update) {

        User user = new User();
        Long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();

        for (User us : userList) {
            if (us.getChatId().equals(chatId)) {
                user = us;
            }
        }

        if (user.getChatId() == null) {
            user = new User(chatId, new Date(), Mode.NONE);
            userList.add(user);
            connectionU.createNewUser(chatId);
        }

        Message message;
        SendMessage sendMessage = new SendMessage();
        try {
            if (user.getMode() == Mode.NONE) {
                message = new NoneModeMessage();
                sendMessage = message.processUpdate(update, user);
            } else if (user.getMode() == Mode.REGISTRATION) {
                message = new RegistrationModeMessage();
                sendMessage = message.processUpdate(update, user);
            } else if (user.getMode() == Mode.TRAINING) {
                message = new TrainingModeMessage();
                sendMessage = message.processUpdate(update, user, questionPool);
            } else if (user.getMode() == Mode.SELFCARE_DEVICE_INFO) {
                message = new SelfcareDeviceInfoModeMessage();
                sendMessage = message.processUpdate(update, user);
            } else if (user.getMode() == Mode.DELETE_USER) {
                message = new DeleteModeMessage();
                sendMessage = message.processUpdate(update, userList, user);
            } else {
                message = new NoneModeMessage();
                sendMessage = message.processUpdate(update, user);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sendMessage.setChatId(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}