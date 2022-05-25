/**
 * Обработка сообщений от пользователя в режиме регистрации самоспасателя
 * processUpdate() - в зависимости от типа сообщения, обрабатывает полученное сообщение и формирует ответ в sendMessage для отправки пользователю.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sberbank.katkova.chatbot.device.SelfCareDeviceRepository;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.scheduler.Scheduler;
import ru.sberbank.katkova.chatbot.scheduler.SchedulerRepository;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;
import ru.sberbank.katkova.chatbot.device.SelfCareDevice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelfcareDeviceInfoModeMessage extends Message {
    private static final String ENTER_MODEL = "Введите модель самоспасателя";
    private static final String ENTER_EXPIRATION_DATE = "Введите срок годности самоспасателя в формате ДД.ММ.ГГГГ";
    private static final String ALREADY_REGISTERED = "Самоспасатель уже зарегистрирован";
    private static final String THANKS_FOR_DEVICE_REGISTRATION = "Благодарим за регистрацию самоспасателя.";
    private static final String DEVICE_IS_EXPIRED = "Ваш самоспасатель просрочен";
    private static final String MODEL_IS = "Вы ввели модель самоспасателя: ";
    private static final String DATE_IS = "Вы ввели дату: ";
    private static final String IF_INCORRECT = ". Если вы ошиблись, введите /clear, чтобы начать регистрацию заново.\n";
    private static final String FINISH_REGISTRATION = "Закончить регистрацию";
    private static final String IF_CORRECT = "Если все верно, нажмите:";
    private static final String DATE_FORMAT_IS_INCORRECT = "Введена дата в неверном формате. Попробуйте ввести дату в формате ДД.ММ.ГГГГ";
    private static final String ATTENTION_DEVICE_IS_EXPIRED = "\nОбратите внимание, что ваш самоспасатель просрочен!";

    @Override
    public SendMessage processUpdate(Update update, User user) {

        SendMessage sendMessage = new SendMessage();
        SelfCareDeviceRepository connectionD = new SelfCareDeviceRepository();
        SchedulerRepository connectionS = new SchedulerRepository();


        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("enterDeviceData")) {
                if (user.getSelfCareDevice() == null || user.getSelfCareDevice().getModel() == null) {
                    sendMessage.setText(ENTER_MODEL);
                } else if (user.getSelfCareDevice() != null && user.getSelfCareDevice().getExpirationDate() == null) {
                    sendMessage.setText(ENTER_EXPIRATION_DATE);
                } else {
                    sendMessage.setText(ALREADY_REGISTERED);
                }
            } else if (update.getCallbackQuery().getData().equals("finishDevice")) {
                java.sql.Date sqlDate = new java.sql.Date(user.getSelfCareDevice().getExpirationDate().getTime());
                connectionD.createNewDevice(user.getChatId(), user.getSelfCareDevice().getModel(), sqlDate);
                sendMessage.setText(THANKS_FOR_DEVICE_REGISTRATION);
                user.setMode(Mode.NONE);
                String scheduleText = DEVICE_IS_EXPIRED;
                Date currentDate = new Date();
                long delay = user.getSelfCareDevice().getExpirationDate().getTime() - currentDate.getTime();
                if (delay > 0) {
                    user.addTask(new Scheduler(user.getChatId(), scheduleText, delay));
                    connectionS.createNewSchedule(user.getChatId(), scheduleText, delay, null);
                } else sendMessage.setText(sendMessage.getText() + ATTENTION_DEVICE_IS_EXPIRED);
            }
        } else if (update.getMessage().hasText() && !update.getMessage().isCommand()) {
            if (user.getSelfCareDevice() == null) {
                user.setSelfCareDevice(new SelfCareDevice());
                user.getSelfCareDevice().setUserId(user.getChatId());
                user.getSelfCareDevice().setModel(update.getMessage().getText());
                sendMessage.setText(MODEL_IS + user.getSelfCareDevice().getModel() + IF_INCORRECT + ENTER_EXPIRATION_DATE);
            } else if (user.getSelfCareDevice() != null && user.getSelfCareDevice().getModel() == null) {
                user.getSelfCareDevice().setModel(update.getMessage().getText());
                sendMessage.setText(MODEL_IS + user.getSelfCareDevice().getModel() + IF_INCORRECT + ENTER_EXPIRATION_DATE);
            } else if (user.getSelfCareDevice() != null && user.getSelfCareDevice().getModel() != null
                    && user.getSelfCareDevice().getExpirationDate() == null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date = dateFormat.parse(update.getMessage().getText());
                    user.getSelfCareDevice().setExpirationDate(date);
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText(FINISH_REGISTRATION).setCallbackData("finishDevice");
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> list = new ArrayList<>();
                    list.add(inlineKeyboardButton);
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(list);
                    inlineKeyboardMarkup.setKeyboard(rowList);
                    String dateFormatted = dateFormat.format(user.getSelfCareDevice().getExpirationDate());
                    sendMessage.setText(DATE_IS + dateFormatted + IF_INCORRECT + IF_CORRECT).setReplyMarkup(inlineKeyboardMarkup);
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                } catch (java.text.ParseException e) {
                    sendMessage.setText(DATE_FORMAT_IS_INCORRECT);
                    return sendMessage;
                }
            }
        } else if (update.getMessage().hasText() && update.getMessage().getText().equals("/clear")) {
            user.getSelfCareDevice().setModel(null);
            user.getSelfCareDevice().setExpirationDate(null);
            sendMessage.setText(ENTER_MODEL);
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
