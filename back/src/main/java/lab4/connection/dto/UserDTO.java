package lab4.connection.dto;

import lab4.database.entity.enums.Role;
import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Role role;
}
