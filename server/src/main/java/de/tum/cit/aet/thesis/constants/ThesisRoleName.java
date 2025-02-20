package de.tum.cit.aet.thesis.constants;

public enum ThesisRoleName {
    STUDENT("STUDENT"),
    ADVISOR("ADVISOR"),
    SUPERVISOR("SUPERVISOR");

    private final String value;

    ThesisRoleName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
