package mx.edu.utez.uxvibe.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class UserRole {
    public static final String ADMIN = "admin";
    public static final String EVALUATOR = "evaluator";
    public static final String PARTICIPANT = "participant";

    private static final Set<String> ALLOWED_ROLES = new LinkedHashSet<>(Arrays.asList(
            ADMIN,
            EVALUATOR,
            PARTICIPANT
    ));

    private UserRole() {
    }

    public static String normalize(String role) {
        return role == null ? "" : role.trim().toLowerCase();
    }

    public static boolean isValid(String role) {
        return ALLOWED_ROLES.contains(normalize(role));
    }
}
