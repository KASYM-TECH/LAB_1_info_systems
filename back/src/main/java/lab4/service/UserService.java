package lab4.service;

import lab4.connection.controller.WebSocketController;
import lab4.database.entity.RoleRequest;
import lab4.database.entity.User;
import lab4.database.entity.enums.Role;
import lab4.database.repository.RoleRequestRepository;
import lab4.database.repository.UserRepository;
import lab4.service.interfaces.IUserService;
import lab4.exception.ForbiddenException;
import lab4.exception.UserDoesNotExistException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private UserRepository repository;
    private RoleRequestRepository roleRequestRepository;
    private IJwtService jwtService;
    private WebSocketController webSocketController;

    @Override
    public User findUserByUserNameAndPassword(String username, String password) {
        return repository.findByNamePassword(username, password);
    }

    @Override
    public void addUser(User user) {
        repository.save(user);
    }

    @Override
    public RoleRequest requestAccess(User user, Role role) {
        var rq = new RoleRequest();
        rq.setUser(user);
        rq.setRole(role);
        roleRequestRepository.save(rq);
        return rq;
    }

    @Override
    public List<RoleRequest> getUsersByRole(Role role, String token) throws UserDoesNotExistException, ForbiddenException {
        var userId = jwtService.getUserIdFromToken(token);
        var user = repository.findById(userId);
        if(user.isEmpty()) throw new UserDoesNotExistException();

        if(user.get().getRole() != Role.Admin) {
            throw new ForbiddenException();
        }

        return roleRequestRepository.getByRole(role);
    }

    @Transactional
    @Override
    public void fulfillRoleRequest(Long roleRequestId, String token) throws UserDoesNotExistException, ForbiddenException {
        var userId = jwtService.getUserIdFromToken(token);
        var user = repository.findById(userId);
        if(user.isEmpty()) throw new UserDoesNotExistException();

        var fetchedRoleRequest = roleRequestRepository.findById(roleRequestId).orElseThrow();

        if(user.get().getRole() != Role.Admin) {
            throw new ForbiddenException();
        }

        var role = roleRequestRepository.fulfill(fetchedRoleRequest.getId());
        userRepository.changeStatus(fetchedRoleRequest.getUser().getId(), role.toString());

        if(fetchedRoleRequest.getRole() == Role.Admin){
            webSocketController.update(String.valueOf(fetchedRoleRequest.getUser().getId()));
        }
    }
}
