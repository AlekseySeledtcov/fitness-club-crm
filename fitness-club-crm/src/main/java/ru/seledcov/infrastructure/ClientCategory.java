package ru.seledcov.infrastructure;

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
}
