package de.tum.cit.aet.thesis.constants;

public enum ThesisState {
    PROPOSAL("PROPOSAL"),
    WRITING("WRITING"),
    SUBMITTED("SUBMITTED"),
    ASSESSED("ASSESSED"),
    GRADED("GRADED"),
    FINISHED("FINISHED"),
    DROPPED_OUT("DROPPED_OUT");

    private final String value;

    ThesisState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
