/**
 * Класс взаимодействия сущности SelfCareDevice с БД
 * createDeviceTable() - создание таблицы с объектами самоспасателей - DEVICES (если еще не существует);
 * createNewDevice() - добавление нового объекта Самоспасатель в базу;
 * restoreDeviceFromDB() - получение данных по самоспасателю пользователя из базы (при старте приложения).
 */

package ru.sberbank.katkova.chatbot.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class SelfCareDeviceRepository {
    private static final String URL_FILE = "jdbc:h2:~/Java_Sber/katkova.chatbot/db/test";
    private static final String USER = "sa";
    private static final String PASSWD = "";
    private static Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(SelfCareDeviceRepository.class);

    static Connection getConnection() throws SQLException {
        connection = connection == null ? DriverManager.getConnection(URL_FILE, USER, PASSWD) : connection;
        return connection;
    }

    public void createDeviceTable() {
        try {
            Statement st = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS DEVICES (userId INTEGER PRIMARY KEY, model VARCHAR(255), expirationDate TIMESTAMP)";
            st.execute(sql);
            LOGGER.info("Создана таблица DEVICES");
        } catch (SQLException e) {
            LOGGER.error("Не удалось созать таблицу DEVICES", e);
        }
    }

    public void createNewDevice(long chatId, String model, Date expirationDate) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO DEVICES (userId, model, expirationDate) VALUES (?, ?, ?)");
            ps.setLong(1, chatId);
            ps.setString(2, model);
            ps.setDate(3, expirationDate);
            ps.executeUpdate();
            LOGGER.info("Самоспасатель пользователя с chatId = " + chatId + " добавлен в базу");
        } catch (SQLException e) {
            LOGGER.error("Не удалось добавить в базу самоспасатель пользователя с chatId = " + chatId, e);
        }
    }

    public SelfCareDevice restoreDeviceFromDB(Long chatId) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT userId, model, expirationDate FROM DEVICES where userId = ?");
            ps.setLong(1, chatId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                SelfCareDevice device = new SelfCareDevice(resultSet.getLong("userId"), resultSet.getString("model"), resultSet.getDate("expirationDate"));
                LOGGER.info("Самоспасатель пользователя с chatId = " + chatId + " загружен из базы");
                return device;
            } else {
                LOGGER.info("Самоспасатель пользователя с chatId = " + chatId + " отсутствует");
                return new SelfCareDevice();
            }
        } catch (SQLException e) {
            LOGGER.error("Не удалось загрузить из базы самоспасатель пользователя с chatId + " + chatId);
            throw new RuntimeException(e);
        }
    }

    public void deleteDevice(Long userId) {
        if (userId != null) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("DELETE FROM DEVICES WHERE userId = ?");
                ps.setLong(1, userId);
                ps.executeUpdate();
                LOGGER.info("Данные самоспасателя пользователя с chatId = " + userId + " удалены из базы");
            } catch (SQLException e) {
                LOGGER.error("Не удалось удалить данные самоспасателя пользователя с chatId = " + userId, e);
            }
        }
    }
}
