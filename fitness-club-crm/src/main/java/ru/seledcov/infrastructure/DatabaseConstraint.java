package ru.seledcov.infrastructure;

public enum DatabaseConstraint {

    UK_CLIENT_EMAIL("uk_client_email"),
    UK_CLIENT_PHONE("uk_client_phone"),
    UNKNOWN("unknown");

    private final String constraintName;

    DatabaseConstraint(String constraintName) {
        this.constraintName = constraintName;
    }

    public static DatabaseConstraint fromConstraintName(String constraintName) {
        for (DatabaseConstraint constraint : values()) {
            if (constraint.constraintName.equals(constraintName)) {
                return constraint;
            }
        }
        return DatabaseConstraint.UNKNOWN;
    }
}
