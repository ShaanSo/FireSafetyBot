/**
 * Класс добавления задачи отправки сообщения пользователю бота по расписанию
 * Метод reminder() - в зависимости от наличия параметра period, планирует отправку сообщения один раз или много раз через заданный период времени.
 */

package ru.sberbank.katkova.chatbot.scheduler;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.sberbank.katkova.chatbot.bot.Bot;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends Bot {

    private String taskId;
    private Long userId;

    private TimerTask timerTask;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Scheduler(Long userId, String reminderText, Long delay, Long period) {
        this.userId = userId;
        this.timerTask = createTask(userId, reminderText);
        Timer timer = new Timer();
        timer.schedule(timerTask, delay, period);
    }

    public Scheduler(Long userId, String reminderText, Long delay) {
        this.userId = userId;
        this.timerTask = createTask(userId, reminderText);
        Timer timer = new Timer();
        timer.schedule(timerTask, delay);
    }

    public TimerTask createTask(Long userId, String reminderText) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(reminderText);
                sendMessage.setChatId(userId);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        };
        return timerTask;
    }
}
