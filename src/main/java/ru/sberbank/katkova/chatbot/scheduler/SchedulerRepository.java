/**
 * Класс взаимодействия сущности Scheduler с БД
 * createUsersTable() - создание таблицы пользователей USERS (если еще не существует, при первом старте приложения);
 * createNewUser() - создание в таблице USERS нового пользователя (при первом взаимодействии с ботом);
 * updateUser() - обновление данных по пользователю в таблице USERS (после прохождения пользователем регистрации);
 * restoreUsersFromDB() - получение объектов пользователей из базы (при старте приложения).
 */

package ru.sberbank.katkova.chatbot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulerRepository {
    private static final String URL_FILE = "jdbc:h2:~/Java_Sber/katkova.chatbot/db/test";
    private static final String USER = "sa";
    private static final String PASSWD = "";
    private static Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerRepository.class);


    static Connection getConnection() throws SQLException {
        connection = connection == null ? DriverManager.getConnection(URL_FILE, USER, PASSWD) : connection;
        return connection;
    }

    public void createSchedulesTable() {
        try {
            Statement st = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS SCHEDULES (taskId VARCHAR(255) PRIMARY KEY, chatId INTEGER, reminderText VARCHAR(255), delay BIGINT, period BIGINT)";
            st.execute(sql);
            LOGGER.info("Создана таблица SCHEDULES");
        } catch (SQLException e) {
            LOGGER.error("Не удалось создать таблицу SCHEDULES", e);
        }
    }

    public void createNewSchedule(long chatId, String reminderText, Long delay, Long period) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO SCHEDULES (taskId, chatId, reminderText, delay, period) VALUES (?, ?, ?, ?, ?)");
            UUID uuid = UUID.randomUUID();
            ps.setString(1, uuid.toString());
            ps.setLong(2, chatId);
            ps.setString(3, reminderText);
            ps.setLong(4, delay);
            if (period != null) {
                ps.setLong(5, period);
            } else ps.setNull(5, Types.BIGINT);
            ps.executeUpdate();
            LOGGER.info("Создано новое задание в таблице SCHEDULES");
        } catch (SQLException e) {
            LOGGER.error("Не удалось создать новое задание в таблице SCHEDULES", e);
        }
    }

    public List<Scheduler> restoreSchedulesFromDB() {
        List<Scheduler> schedulerList = new ArrayList<>();
        try {
            Statement st = getConnection().createStatement();
            String sql = "SELECT chatId, reminderText, delay, period FROM SCHEDULES";
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                if (resultSet.getObject("period") != null) {
                    schedulerList.add(new Scheduler(resultSet.getLong("chatId"), resultSet.getString("reminderText"), resultSet.getLong("delay"), resultSet.getLong(
                            "period")));
                } else {
                    schedulerList.add(new Scheduler(resultSet.getLong("chatId"), resultSet.getString("reminderText"), resultSet.getLong("delay")));
                }
            }
            LOGGER.info("Данные по задачам восстановлены из базы");
            return schedulerList;
        } catch (SQLException e) {
            LOGGER.error("Не удалось восстановить данные по задачам из базы");
            throw new RuntimeException(e);
        }
    }

    public void deleteScheduler(Long chatId) {
        if (chatId != null) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM SCHEDULES WHERE chatId = ?");
                ps.setLong(1, chatId);
                ps.executeUpdate();
                LOGGER.info("Задачи пользователя с chatId = " + chatId + " удалены из базы");
            } catch (SQLException e) {
                LOGGER.error("Не удалось удалить задачи пользователя с chatId = " + chatId, e);
            }
        }
    }
}