package ru.seledcov.infrastructure;

public enum AccessTime implements NamedEnum{
    DAY("day"),
    ALL_DAY("allDay");

    private final String name;

    AccessTime(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
