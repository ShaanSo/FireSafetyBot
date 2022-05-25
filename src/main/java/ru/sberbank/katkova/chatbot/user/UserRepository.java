/**
 * Класс взаимодействия сущности User с БД
 * createUsersTable() - создание таблицы пользователей USERS (если еще не существует, при первом старте приложения);
 * createNewUser() - создание в таблице USERS нового пользователя (при первом взаимодействии с ботом);
 * updateUser() - обновление данных по пользователю в таблице USERS (после прохождения пользователем регистрации);
 * restoreUsersFromDB() - получение объектов пользователей из базы (при старте приложения);
 * deleteUser() - удаление пользователя из таблицы USERS.
 */

package ru.sberbank.katkova.chatbot.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String URL_FILE = "jdbc:h2:~/Java_Sber/katkova.chatbot/db/test";
    private static final String USER = "sa";
    private static final String PASSWD = "";
    private static Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

    static Connection getConnection() throws SQLException {
        connection = connection == null ? DriverManager.getConnection(URL_FILE, USER, PASSWD) : connection;
        return connection;
    }

    public void createUsersTable() {
        try {
            Statement st = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS USERS (chatId INTEGER PRIMARY KEY, firstName VARCHAR(255), lastName VARCHAR(255), employmentDate TIMESTAMP, createDateTime TIMESTAMP)";
            st.execute(sql);
            LOGGER.info("Создана таблица USERS");
        } catch (SQLException e) {
            LOGGER.error("Не удалось создать таблицу USERS", e);
        }
    }

    public void createNewUser(long chatId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO USERS (chatId, firstName, lastName, employmentDate, createDateTime) VALUES (?, null, null, null, NOW())");
            ps.setLong(1, chatId);
            ps.executeUpdate();
            LOGGER.info("Создан новый пользователь в таблице USERS");
        } catch (SQLException e) {
            LOGGER.error("Не удалось создать нового пользователя в таблице USERS", e);
        }
    }

    public void updateUser(long chatId, String firstName, String lastName, Date employmentDate) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE USERS SET firstName = ?, lastName = ?, employmentDate = ? WHERE chatId = ?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, employmentDate);
            ps.setLong(4, chatId);
            ps.executeUpdate();
            LOGGER.info("Данные пользователя с chatId = " + chatId + " обновлены в базе");
        } catch (SQLException e) {
            LOGGER.error("Не удалось обновить данные пользователя с chatId = " + chatId, e);
        }
    }

    public List<User> restoreUsersFromDB() {
        List<User> userList = new ArrayList<>();
        try {
            Statement st = getConnection().createStatement();
            String sql = "SELECT chatId, firstName, lastName, employmentDate, createDateTime FROM USERS";
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                userList.add(new User(resultSet.getLong("chatId"), resultSet.getDate("createDateTime"), Mode.NONE, resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getDate("employmentDate")));
            }
            LOGGER.info("Данные пользователей восстановлены из базы");
            return userList;
        } catch (SQLException e) {
            LOGGER.error("Не удалось восстановить данные пользователей из базы");
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long chatId) {
        if (chatId != null) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM USERS WHERE chatId = ?");
                ps.setLong(1, chatId);
                ps.executeUpdate();
                LOGGER.info("Данные пользователя с chatId = " + chatId + " удалены из базы");
            } catch (SQLException e) {
                LOGGER.error("Не удалось удалить данные пользователя с chatId = " + chatId, e);
            }
        }
    }
}