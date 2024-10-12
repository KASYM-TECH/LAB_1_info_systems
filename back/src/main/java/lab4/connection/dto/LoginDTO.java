package lab4.connection.dto;

import lab4.database.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    private long userId;
    private String username;
    private String token;
    private Role role;
}
