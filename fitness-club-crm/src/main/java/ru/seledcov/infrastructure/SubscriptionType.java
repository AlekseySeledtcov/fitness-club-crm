package ru.seledcov.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubscriptionType implements NamedEnum {
    SELF_SERVICE("selfService"),
    GROUP("group"),
    PERSONAL("personal"),
    MINI_GROUP("miniGroup");

    private final String name;

    SubscriptionType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SubscriptionType fromName(String name) {
        return EnumUtils.fromName(SubscriptionType.class, name);
    }
}
