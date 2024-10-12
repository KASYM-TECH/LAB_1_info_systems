package lab4.service.interfaces;

import lab4.database.entity.RoleRequest;
import lab4.database.entity.User;
import lab4.database.entity.enums.Role;
import lab4.exception.ForbiddenException;
import lab4.exception.UserDoesNotExistException;

import java.util.List;


public interface IUserService {
    User findUserByUserNameAndPassword(String username, String password);
    RoleRequest requestAccess(User user, Role role);
    List<RoleRequest> getUsersByRole(Role role, String token) throws UserDoesNotExistException, ForbiddenException;
    void addUser(User user);
    void fulfillRoleRequest(Long roleRequestId, String token) throws UserDoesNotExistException, ForbiddenException;
}
