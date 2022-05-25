/**
 * Обработка сообщений от пользователя в "стартовом" режиме
 * processUpdate() - в зависимости от типа сообщения, обрабатывает полученное сообщение и формирует ответ в sendMessage для отправки пользователю.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sberbank.katkova.chatbot.bot.Command;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;

import java.util.ArrayList;
import java.util.List;

public class NoneModeMessage extends Message {

    private static final String REGISTER = "Зарегистрироваться";
    private static final String TO_REGISTER_PRESS = "Чтобы зарегистрироваться, нажмите:";
    private static final String ALREADY_REGISTERED = "Вы уже зарегистрированы!";
    private static final String GO_TRAINING = "Пройти инструктаж";
    private static final String TO_TRAIN_PRESS = "Чтобы начать инструктаж, нажмите";
    private static final String ENTER_INFO = "Ввести информацию";
    private static final String TO_ENTER_DEVICE_INFO_PRESS = "Чтобы заполнить данные по самоспасателю, нажмите";
    private static final String DELETE = "Подтвердить удаление";
    private static final String CANCEL = "Отменить";
    private static final String ARE_YOU_SURE_TO_DELETE = "Вы действительно хотите удалить пользователя и отменить все, связанные с ним задачи?";

    @Override
    public SendMessage processUpdate(Update update, User user) {

        SendMessage sendMessage = new SendMessage();

        if (update.hasCallbackQuery()) {
            sendMessage.setText(WELCOME_TEXT);
        } else {
            if (update.getMessage().getText().equals(Command.REGISTER.getValue()) && !user.isRegistered()) {
                user.setMode(Mode.REGISTRATION);
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(REGISTER).setCallbackData("register");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list = new ArrayList<>();
                list.add(inlineKeyboardButton);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText(TO_REGISTER_PRESS).setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            } else if (update.getMessage().getText().equals(Command.REGISTER.getValue()) && user.isRegistered()) {
                sendMessage.setText(ALREADY_REGISTERED);
            } else if (update.getMessage().getText().equals(Command.START_TRAINING.getValue())) {
                user.setMode(Mode.TRAINING);
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(GO_TRAINING).setCallbackData("startTraining");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list = new ArrayList<>();
                list.add(inlineKeyboardButton);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText(TO_TRAIN_PRESS).setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            } else if (update.getMessage().getText().equals(Command.SELFCARE_DEVICE_INFO.getValue())) {
                user.setMode(Mode.SELFCARE_DEVICE_INFO);
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(ENTER_INFO).setCallbackData("enterDeviceData");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list = new ArrayList<>();
                list.add(inlineKeyboardButton);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText(TO_ENTER_DEVICE_INFO_PRESS).setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            } else if (update.getMessage().getText().equals(Command.DELETE_USER.getValue())) {
                user.setMode(Mode.DELETE_USER);
                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText(DELETE).setCallbackData("delete");
                InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
                inlineKeyboardButton2.setText(CANCEL).setCallbackData("cancel");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list1 = new ArrayList<>();
                list1.add(inlineKeyboardButton1);
                List<InlineKeyboardButton> list2 = new ArrayList<>();
                list2.add(inlineKeyboardButton2);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list1);
                rowList.add(list2);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setText(ARE_YOU_SURE_TO_DELETE).setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            } else sendMessage.setText(WELCOME_TEXT);
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
