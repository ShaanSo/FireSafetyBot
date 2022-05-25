/**
 * Enum для перечисления режимов взаимодействия с ботом:
 * NONE - стартовый режим;
 * REGISTRATION - регистрация пользователя;
 * SELFCARE_DEVICE_INFO - регистрация самоспасателя;
 * TRAINING - прохождение инструктажа;
 * DELETE_USER - удаление пользователя.
 */

package ru.sberbank.katkova.chatbot.user;

public enum Mode {
    NONE,
    REGISTRATION,
    SELFCARE_DEVICE_INFO,
    TRAINING,
    DELETE_USER
}