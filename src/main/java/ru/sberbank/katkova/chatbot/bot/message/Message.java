/**
 * Класс обработки сообщения, пришедшего от пользователя. Метод processUpdate() перегружен с разными сигнатурами и переопределен в классах-наследниках.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.user.User;

import java.text.ParseException;
import java.util.List;

abstract public class Message extends Update {
    public static final String WELCOME_TEXT = "Уважаемый коллега! \n" +
            "Этот бот поможет вам пройти инструктаж по пожарной безопасности.\n" +
            "Чтобы начать работу необходимо зарегистрироваться. Это можно сделать командой /register.\n" +
            "Чтобы пройти интсруктаж по технике пожарной безопасности, введите команду /training.\n" +
            "Чтобы ввести данные по самоспасателю, используйте команду /device.\n" +
            "Чтобы удалить пользователя и отменить все, связанные с ним задачи, используйте команду /delete.\n" +
            "Чтобы снова увидеть меню используйте команду /start.";

    abstract public SendMessage processUpdate(Update update, User user) throws ParseException;

    abstract public SendMessage processUpdate(Update update, User user, List<Question> questionPool) throws ParseException;

    abstract public SendMessage processUpdate(Update update, List<User> userList, User user);
}
