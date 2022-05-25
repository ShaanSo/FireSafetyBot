/**
 * Обработка сообщений от пользователя в режиме удаления пользователя.
 * processUpdate() - в зависимости от типа сообщения обрабатывает полученное сообщение и формирует ответ в sendMessage для отправки пользователю.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import ru.sberbank.katkova.chatbot.device.SelfCareDeviceRepository;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.scheduler.Scheduler;
import ru.sberbank.katkova.chatbot.scheduler.SchedulerRepository;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;
import ru.sberbank.katkova.chatbot.user.UserRepository;

import java.util.List;

public class DeleteModeMessage extends Message {

    private static final String DATA_HAVE_BEEN_DELETED = "Ваши данные и задачи были удалены из базы";

    @Override
    public SendMessage processUpdate(Update update, List<User> userList, User user) {
        SendMessage sendMessage = new SendMessage();
        UserRepository connectionU = new UserRepository();
        SchedulerRepository connectionS = new SchedulerRepository();
        SelfCareDeviceRepository connectionD = new SelfCareDeviceRepository();

        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("delete")) {
                connectionD.deleteDevice(user.getChatId());
                connectionS.deleteScheduler(user.getChatId());
                connectionU.deleteUser(user.getChatId());
                int i = 0;
                int userIndex = -1;
                for (User u : userList) {
                    if (user.getChatId() == u.getChatId()) {
                        userIndex = i;
                        if (u.getTaskList() != null) {
                            for (Scheduler task : user.getTaskList()) {
                                task.getTimerTask().cancel();
                            }
                        }
                        u.deleteUserData();
                    }
                    i++;
                }
                if (userIndex != -1) {
                    userList.remove(userIndex);
                }
                return sendMessage.setText(DATA_HAVE_BEEN_DELETED);
            } else if (update.getCallbackQuery().getData().equals("cancel")) {
                user.setMode(Mode.NONE);
                return sendMessage.setText(WELCOME_TEXT);
            }
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
    public SendMessage processUpdate(Update update, User user) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setText(WELCOME_TEXT);
    }
}