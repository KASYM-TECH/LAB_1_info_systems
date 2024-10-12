package lab4.service.interfaces;

import jakarta.servlet.http.HttpServletResponse;
import lab4.connection.dto.LoginDTO;
import lab4.database.entity.User;
import lab4.exception.InvalidUserCredentialsException;
import lab4.exception.UserDoesNotExistException;
import lab4.exception.UsernameOccupiedException;

public interface IAuthService {
    LoginDTO registerNewUser(User user, HttpServletResponse response) throws UsernameOccupiedException, UserDoesNotExistException, InvalidUserCredentialsException;
    LoginDTO authUser(User user, HttpServletResponse response) throws UserDoesNotExistException, InvalidUserCredentialsException;
}
