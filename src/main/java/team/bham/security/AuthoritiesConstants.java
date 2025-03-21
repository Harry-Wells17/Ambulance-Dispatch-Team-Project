package team.bham.security;

/**
 * Constants for Spring Security authorities.
 *
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String MANAGEMENT = "ROLE_MANAGEMENT";

    public static final String CONTROL_ROOM_MANAGER = "ROLE_CONTROL_ROOM_MANAGER";

    public static final String DISPATCHER = "ROLE_DISPATCHER";

    public static final String LOGGIST = "ROLE_LOGGIST";

    public static final String CLINICIAN = "ROLE_CLINICIAN";

    public static final String USER = "ROLE_USER";

    private AuthoritiesConstants() {}
}
