package team.bham.domain.enumeration;

/**
 * The LogType enumeration.
 */
public enum LogType {
    CONTROLROOM("Control_Room"),
    CALL("Call_Log"),
    RESOURCE("Resource_Log"),
    BREAKLOG("Break_Log");

    private final String value;

    LogType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
