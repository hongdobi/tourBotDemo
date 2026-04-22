package org.tourBot.domain;

public enum Role {
    USER,
    ASSISTANT;

    public static Role from(String value) {
        return Role.valueOf(value.toUpperCase());
    }

    public String toValue() {
        return this.name().toLowerCase();
    }
}
