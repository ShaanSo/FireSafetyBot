/**
 * Класс пользователя (модель данных)
 * chatId - идентификатор взаимодействия с ботом;
 * firstName - имя пользователя;
 * lastName - фамилия пользователя;
 * employmentDate - дата трудоустройства;
 * createDateTime - время и дата создания пользователя;
 * mode - текущий режим взаимодействия пользователя с ботом;
 * selfCareDevice - привязанный к пользователю самоспасатель, объект класса SelfCareDevice;
 * currentQuestion - текущий вопрос пользователя в инструктаже;
 * taskList - список уведомлений от бота, которые будут приходить пользователю (список объектов класса Scheduler).
 */

package ru.sberbank.katkova.chatbot.user;

import ru.sberbank.katkova.chatbot.device.SelfCareDevice;
import ru.sberbank.katkova.chatbot.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private Long chatId;
    private String firstName;
    private String lastName;
    private Date employmentDate;
    private Date createDateTime;
    private Mode mode;
    private SelfCareDevice selfCareDevice;
    private int currentQuestion;
    private List<Scheduler> taskList;

    public List<Scheduler> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Scheduler> taskList) {
        this.taskList = taskList;
    }

    public void addTask(Scheduler task) {
        this.taskList.add(task);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isRegistered() {
        return getFirstName() != null && getLastName() != null && getEmploymentDate() != null;
    }

    public SelfCareDevice getSelfCareDevice() {
        return selfCareDevice;
    }

    public void setSelfCareDevice(SelfCareDevice selfCareDevice) {
        this.selfCareDevice = selfCareDevice;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public User(Long chatId, Date createDateTime, Mode mode) {
        this(chatId, createDateTime, mode, null, null, null);
    }

    public User(Long chatId, Date createDateTime, Mode mode, String firstName, String lastName, Date employmentDate) {
        this.chatId = chatId;
        this.mode = mode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employmentDate = employmentDate;
        this.createDateTime = createDateTime;
        this.currentQuestion = 1;
        this.taskList = new ArrayList<>();
    }

    public User() {
    }

    public void deleteUserData() {
        this.setCurrentQuestion(1);
        this.setEmploymentDate(null);
        this.setLastName(null);
        this.setFirstName(null);
        this.setMode(Mode.NONE);
        this.setSelfCareDevice(null);
    }
}