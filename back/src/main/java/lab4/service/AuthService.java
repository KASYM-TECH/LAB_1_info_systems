package lab4.service;

import jakarta.servlet.http.HttpServletResponse;
import lab4.connection.dto.LoginDTO;
import lab4.database.entity.User;
import lab4.database.entity.enums.Role;
import lab4.service.interfaces.IAuthService;
import lab4.service.interfaces.IUserService;
import lab4.exception.InvalidUserCredentialsException;
import lab4.exception.UserDoesNotExistException;
import lab4.exception.UsernameOccupiedException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {
    private final IUserService userService;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public synchronized LoginDTO registerNewUser(User user, HttpServletResponse response) throws UsernameOccupiedException, UserDoesNotExistException, InvalidUserCredentialsException {
        if (userService.findUserByUserNameAndPassword(user.getUsername(), user.getPassword()) != null) {
            throw new UsernameOccupiedException();
        }
        if(user.getRole() == Role.Admin) {
            user.setRole(Role.User);
            var rq = userService.requestAccess(user, Role.Admin);
            return authUser(rq.getUser(), response);
        }
        userService.addUser(user);
        return authUser(user, response);
    }

    @Override
    @Transactional
    public LoginDTO authUser(User user, HttpServletResponse response) throws UserDoesNotExistException, InvalidUserCredentialsException {
        var foundUser = userService.findUserByUserNameAndPassword(user.getUsername(), user.getPassword());
        if (foundUser == null) {
            throw new UserDoesNotExistException();
        }
        final String token = jwtService.generateAccessToken(foundUser.getUsername(), foundUser.getId(), foundUser.getRole());
        return new LoginDTO(foundUser.getId(), foundUser.getUsername(), token, foundUser.getRole());
    }
}
