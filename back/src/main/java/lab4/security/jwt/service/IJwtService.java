package lab4.security.jwt.service;

import lab4.database.entity.enums.Role;

public interface IJwtService {
    String generateAccessToken(String username, Long userId, Role role);
    Long getUserIdFromToken(String token);
    Role getUserRoleFromToken(String token);
    String getUsernameFromToken(final String token);
}
