package ru.seledcov.exception;

public class TariffAlreadyExistsException extends RuntimeException {
    public TariffAlreadyExistsException(String message) {
        super(message);
    }
}
