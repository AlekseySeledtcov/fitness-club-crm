package ru.seledcov.infrastructure;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public class EnumUtils {
    public static <E extends Enum<E> & NamedEnum> E fromName(
            Class<E> enumClass,
            String name
    ) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown subscription type: " + name));
    }
}
