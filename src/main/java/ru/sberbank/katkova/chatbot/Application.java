/**
 * Основной класс приложения с методом main для запуска телеграм-бота.
 * На старте приложения:
 * - создает (при необходимости) таблицы в БД: таблица пользователей, таблица вопросов инструктажа, таблица самоспасателей, таблица уведомлений для пользователей.
 * - восстанавливает данные из БД в сущности классов: User, Question, SelfCareDevice, Scheduler.
 * - создает объект телеграм-бота
 */

package ru.sberbank.katkova.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sberbank.katkova.chatbot.bot.Bot;
import ru.sberbank.katkova.chatbot.device.SelfCareDeviceRepository;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.question.QuestionRepository;
import ru.sberbank.katkova.chatbot.scheduler.SchedulerRepository;
import ru.sberbank.katkova.chatbot.user.User;
import ru.sberbank.katkova.chatbot.user.UserRepository;

import java.util.List;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApiContextInitializer.init();

        UserRepository connectionU = new UserRepository();
        QuestionRepository connectionQ = new QuestionRepository();
        SelfCareDeviceRepository connectionD = new SelfCareDeviceRepository();
        SchedulerRepository connectionS = new SchedulerRepository();

        connectionU.createUsersTable();
        connectionD.createDeviceTable();
        connectionS.createSchedulesTable();

        List<User> userList = connectionU.restoreUsersFromDB();
        for (User u : userList) {
            u.setSelfCareDevice(connectionD.restoreDeviceFromDB(u.getChatId()));
        }

        connectionQ.createQuestionTable();
        List<Question> questionPool = connectionQ.restoreQuestionPoolFromDB();

        connectionS.restoreSchedulesFromDB();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot(questionPool, userList);

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}