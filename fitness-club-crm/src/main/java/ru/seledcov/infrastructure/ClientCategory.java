package ru.seledcov.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ClientCategory implements NamedEnum {
    ADULT("adult"),
    BENEFIT("benefit");

    private final String name;

    ClientCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ClientCategory fromName(String name) {
        return EnumUtils.fromName(ClientCategory.class, name);
    }
}
