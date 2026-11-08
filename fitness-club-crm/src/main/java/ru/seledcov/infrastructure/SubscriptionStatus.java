package ru.seledcov.infrastructure;

public enum SubscriptionStatus implements NamedEnum {
    NOT_ACTIVATED("notActivated"),
    ACTIVE("active"),
    SUSPENDED("suspended"),
    EXPIRED("expired"),
    CANCELLED("cancelled");

    private final String name;

    SubscriptionStatus(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return "name";
    }
}
