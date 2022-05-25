/**
 * Класс объектов Самоспасатель (модель данных)
 * userId - идентификатор пользователя, к которому привязан самоспасатель;
 * model - модель самоспасателя;
 * expirationDate - срок годности самоспасателя;
 */

package ru.sberbank.katkova.chatbot.device;

import java.util.Date;

public class SelfCareDevice {

    private Long userId;
    private String model;
    private Date expirationDate;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SelfCareDevice(Long userId, String model, Date expirationDate) {
        this.userId = userId;
        this.model = model;
        this.expirationDate = expirationDate;
    }

    public SelfCareDevice() {
    }

}
