/**
 * Класс вопросов инструктажа (модель данных)
 * id - номер вопроса;
 * question - формулировка вопроса;
 * answer1 - первый вариант ответа;
 * answer2 - второй вариант ответа;
 * answer3 - третий вариант ответа;
 * index - индекс правильного варианта ответа (1, 2 или 3);
 */

package ru.sberbank.katkova.chatbot.question;

public class Question {

    private int id;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private int index;

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public int getIndex() {
        return index;
    }

    Question(int id, String question, String answer1, String answer2, String answer3, int index) {
        this.id = id;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.index = index;
    }

    public String getCorrectAnswer() {
        switch (this.getIndex()) {
            case 1:
                return this.getAnswer1();
            case 2:
                return this.getAnswer2();
            case 3:
                return this.getAnswer3();
            default:
                return "Правильный ответ отсутствует";
        }
    }
}


