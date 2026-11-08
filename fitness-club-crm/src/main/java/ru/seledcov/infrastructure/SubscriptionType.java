package ru.seledcov.infrastructure;

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
}
