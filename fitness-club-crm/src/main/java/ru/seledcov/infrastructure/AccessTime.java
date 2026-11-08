package ru.seledcov.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;

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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessTime fromName(String name) {
        return EnumUtils.fromName(AccessTime.class, name);
    }
}
