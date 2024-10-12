package lab4.connection.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lab4.connection.HttpCodes;
import lab4.connection.dto.LoginDTO;
import lab4.connection.dto.UserDTO;
import lab4.database.entity.RoleRequest;
import lab4.database.entity.User;
import lab4.database.entity.enums.Role;
import lab4.service.interfaces.IAuthService;
import lab4.service.interfaces.IUserService;
import lab4.exception.InvalidUserCredentialsException;
import lab4.exception.UserDoesNotExistException;
import lab4.exception.UsernameOccupiedException;
import lab4.security.Hasher;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final IAuthService authService;
    private final IUserService userService;
    private WebSocketController webSocketController;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginDTO> login(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            userDTO.setPassword(Hasher.hashWithMD5(userDTO.getPassword()));
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRole(userDTO.getRole());
            user.setPassword(userDTO.getPassword());
            return ResponseEntity.ok(authService.authUser(user, response));
        } catch (UserDoesNotExistException e) {
            return ResponseEntity.status(HttpCodes.UNKNOWN_USER.getCode()).build();
        } catch (InvalidUserCredentialsException e) {
            return ResponseEntity.status(HttpCodes.INVALID_USER_CREDENTIALS.getCode()).build();
        }
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<LoginDTO> signup(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            userDTO.setPassword(Hasher.hashWithMD5(userDTO.getPassword()));
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setRole(userDTO.getRole());
            user.setPassword(userDTO.getPassword());
            LoginDTO jwtDTO = authService.registerNewUser(user, response);
            webSocketController.update("");
            return ResponseEntity.ok(jwtDTO);
        } catch (UsernameOccupiedException ex) {
            return ResponseEntity.status(HttpCodes.USERNAME_ALREADY_USED.getCode()).build();
        } catch (UserDoesNotExistException ex) {
            return ResponseEntity.status(HttpCodes.UNKNOWN_USER.getCode()).build();
        } catch (InvalidUserCredentialsException e) {
            return ResponseEntity.status(HttpCodes.INVALID_USER_CREDENTIALS.getCode()).build();
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<RoleRequest>> getRoleRequests(@RequestParam(value = "role") Role role, HttpServletRequest request) {
        try {
            var res = userService.getUsersByRole(role, request.getHeader("Authorization"));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpCodes.FORBIDDEN.getCode()).build();
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> fulfill(@RequestParam(value = "roleRequestId") Long roleRequestId, HttpServletRequest request) {
        try {
            userService.fulfillRoleRequest(roleRequestId, request.getHeader("Authorization"));
            webSocketController.update("");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpCodes.FORBIDDEN.getCode()).build();
        }
    }
}
