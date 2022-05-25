/**
 * Класс с общими методами работы с опросником.
 * getQuestion() - возвращает объект класса Question по переданному списку вопросов и индексу вопроса.
 * questionCount() - возвращает число вопросов в опроснике.
 */

package ru.sberbank.katkova.chatbot.question;

import java.util.List;

public class QuestionService {

    public static Question getQuestion(List<Question> questionPool, int index) {
        for (Question q : questionPool) {
            if (index == q.getId()) return q;
        }
        return questionPool.get(0);
    }

    public static int questionCount(List<Question> questionPool) {
        int i = 0;
        for (Question q : questionPool) {
            i++;
        }
        return i;
    }
}
