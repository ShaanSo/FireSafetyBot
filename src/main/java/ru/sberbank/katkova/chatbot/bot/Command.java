/**
 * Enum с командами, которые может обрабатывать бот.
 */

package ru.sberbank.katkova.chatbot.bot;

public enum Command {
    START("/start"),
    REGISTER("/register"),
    START_TRAINING("/training"),
    SELFCARE_DEVICE_INFO("/device"),
    DELETE_USER("/delete");

    private String value;

    public String getValue() {
        return value;
    }

    Command(String value) {
        this.value = value;
    }
}
