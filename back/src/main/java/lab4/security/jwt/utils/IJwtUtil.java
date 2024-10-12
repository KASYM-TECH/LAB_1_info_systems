package lab4.security.jwt.utils;

import lab4.database.entity.enums.Role;

public interface IJwtUtil {

    String generateAccessToken(String username, Long userId, Role role);

    String getUserNameFromToken(String token);
    Long getUserIdFromToken(String token);

    boolean validateAccessToken(String token);
    long getTokenIssuedAt(String token);
    Role getUserRoleFromToken(String token);
    long getTokenExpiration(String token);
}
