/**
 * Класс взаимодействия сущности Question с БД
 * createQuestionTable() - создание таблицы вопросов и ответов для инструктажа QUESTIONS (если еще не существует)
 * и наполнения ее через enum QuestionBackUp;
 * restoreQuestionPoolFromDB() - получение вопросов и ответов для инструктажа из базы (при старте приложения).
 */

package ru.sberbank.katkova.chatbot.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    private static final String URL_FILE = "jdbc:h2:~/Java_Sber/katkova.chatbot/db/test";
    private static final String USER = "sa";
    private static final String PASSWD = "";
    private static Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionRepository.class);

    static Connection getConnection() throws SQLException {
        connection = connection == null ? DriverManager.getConnection(URL_FILE, USER, PASSWD) : connection;
        return connection;
    }

    public List<Question> restoreQuestionPoolFromDB() {
        List<Question> questionList = new ArrayList<>();
        try {
            Statement st = getConnection().createStatement();
            String sql = "SELECT id, question, answer1, answer2, answer3, index FROM QUESTIONS";
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                questionList.add(new Question(resultSet.getInt("id"), resultSet.getString("question"), resultSet.getString("answer1"),
                        resultSet.getString("answer2"), resultSet.getString("answer3"), resultSet.getInt("index")));
            }
            LOGGER.info("Данные опросника загружены из базы");
            return questionList;
        } catch (SQLException e) {
            LOGGER.error("Не удалось загрузить данные опросника из базы");
            throw new RuntimeException();
        }
    }

    public void createQuestionTable() {
        try {
            Statement st = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS QUESTIONS (id INTEGER PRIMARY KEY, question VARCHAR(255), answer1 VARCHAR(255), answer2 VARCHAR(255), answer3 VARCHAR(255), index INTEGER)";
            st.execute(sql);
            sql = "SELECT COUNT(*) AS TOTAL FROM QUESTIONS";
            ResultSet resultSet = st.executeQuery(sql);
            resultSet.next();
            int count = resultSet.getInt("TOTAL");
            if (count == 0) {
                for (QuestionBackUp q : QuestionBackUp.values()) {
                    PreparedStatement ps = getConnection().prepareStatement("INSERT INTO QUESTIONS (id, question, answer1, answer2, answer3, index) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, q.getId());
                    ps.setString(2, q.getQuestion());
                    ps.setString(3, q.getAnswer1());
                    ps.setString(4, q.getAnswer2());
                    ps.setString(5, q.getAnswer3());
                    ps.setInt(6, q.getIndex());
                    ps.executeUpdate();
                }
            }
            LOGGER.info("Создана таблица QUESTIONS");
        } catch (SQLException e) {
            LOGGER.error("Не удалось создать и наполнить таблицу QUESTIONS", e);
        }
    }
}
