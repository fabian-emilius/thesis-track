package de.tum.cit.aet.thesis.constants;

public enum ThesisVisibility {
    PRIVATE("PRIVATE"),
    INTERNAL("INTERNAL"),
    STUDENT("STUDENT"),
    PUBLIC("PUBLIC");

    private final String value;

    ThesisVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
