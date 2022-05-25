/**
 * Обработка сообщений от пользователя в режиме прохождения инструктажа.
 * processUpdate() - в зависимости от типа сообщения, обрабатывает полученное сообщение и формирует ответ в sendMessage для отправки пользователю.
 */

package ru.sberbank.katkova.chatbot.bot.message;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.sberbank.katkova.chatbot.question.Question;
import ru.sberbank.katkova.chatbot.question.QuestionService;
import ru.sberbank.katkova.chatbot.user.Mode;
import ru.sberbank.katkova.chatbot.user.User;

import java.util.ArrayList;
import java.util.List;

public class TrainingModeMessage extends Message {
    private static final String CORRECT = "Правильно!";
    private static final String INCORRECT = "Неправильно! Правильный ответ: ";
    private static final String CONGRATULATIONS = "\nПоздравляем! Вы прошли инструктаж.";
    private static final String NEXT_QUESTION = "Следующий вопрос";
    private static final String INTERRUPT = "Прервать";
    private static final String ARE_YOU_SURE_TO_INTERRUPT = "Вы уверены что хотите прервать прохождение инструктажа? " +
            "Нажмите \"Прервать\", чтобы выйти из опроса, или \"Следующий вопрос\", чтобы продолждить инструктаж";

    @Override
    public SendMessage processUpdate(Update update, User user, List<Question> questionPool) {

        SendMessage sendMessage = new SendMessage();

        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("nextQuestion") || update.getCallbackQuery().getData().equals("startTraining")) {
                Question question = QuestionService.getQuestion(questionPool, user.getCurrentQuestion());
                sendMessage.setText(question.getQuestion());
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboardRowList = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardRow keyboardRow2 = new KeyboardRow();
                KeyboardRow keyboardRow3 = new KeyboardRow();
                keyboardRow1.add(question.getAnswer1());
                keyboardRow2.add(question.getAnswer2());
                keyboardRow3.add(question.getAnswer3());
                keyboardRowList.add(keyboardRow1);
                keyboardRowList.add(keyboardRow2);
                keyboardRowList.add(keyboardRow3);
                replyKeyboardMarkup.setKeyboard(keyboardRowList).setResizeKeyboard(true).setOneTimeKeyboard(true);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            } else {
                user.setMode(Mode.NONE);
                user.setCurrentQuestion(0);
                sendMessage.setText(WELCOME_TEXT);
            }

        } else if (update.getMessage().hasText() && !update.getMessage().isCommand()) {
            Question question = QuestionService.getQuestion(questionPool, user.getCurrentQuestion());
            if (update.getMessage().getText().equals(question.getCorrectAnswer())) {
                sendMessage.setText(CORRECT);
            } else {
                sendMessage.setText(INCORRECT + question.getCorrectAnswer());
            }
            user.setCurrentQuestion(user.getCurrentQuestion() + 1);
            if (user.getCurrentQuestion() == QuestionService.questionCount(questionPool)) {
                sendMessage.setText(sendMessage.getText() + CONGRATULATIONS);
                user.setMode(Mode.NONE);
                user.setCurrentQuestion(1);
            } else {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(NEXT_QUESTION).setCallbackData("nextQuestion");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> list = new ArrayList<>();
                list.add(inlineKeyboardButton);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(list);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            }
        } else {
            sendMessage.setText(ARE_YOU_SURE_TO_INTERRUPT);
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(INTERRUPT).setCallbackData("interruptTraining");
            inlineKeyboardButton2.setText(NEXT_QUESTION).setCallbackData("nextQuestion");
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> list1 = new ArrayList<>();
            list1.add(inlineKeyboardButton1);
            List<InlineKeyboardButton> list2 = new ArrayList<>();
            list2.add(inlineKeyboardButton2);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(list1);
            rowList.add(list2);
            inlineKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        return sendMessage;
    }

    @Override
    public SendMessage processUpdate(Update update, User user) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setText(WELCOME_TEXT);
    }

    @Override
    public SendMessage processUpdate(Update update, List<User> userList, User user) {
        SendMessage sendMessage = new SendMessage();
        return sendMessage.setText(WELCOME_TEXT);
    }
}
