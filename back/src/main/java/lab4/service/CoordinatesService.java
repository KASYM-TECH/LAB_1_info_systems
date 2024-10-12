package lab4.service;

import lab4.database.entity.Coordinates;
import lab4.database.entity.enums.Role;
import lab4.database.repository.CoordinatesRepository;
import lab4.database.repository.MovieRepository;
import lab4.exception.ForbiddenException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CoordinatesService {
    private CoordinatesRepository coordinatesRepository;
    private MovieRepository movieRepository;
    private IJwtService jwtService;

    public Coordinates createCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    public Coordinates getCoordinatesById(Long id) throws Exception {
        return coordinatesRepository.findById(id).orElseThrow(() -> new Exception("Coordinates not found"));
    }

    public List<Coordinates> getAll() {
        return coordinatesRepository.findAll();
    }

    @Transactional
    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates, String token) throws Exception {
        var coordinates = coordinatesRepository.findById(id).stream().findFirst().orElseThrow();
        var userRole = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        if(coordinates.getCreatorId() != userId) {
            if(userRole != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!coordinates.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }

        coordinates.setX(updatedCoordinates.getX());
        coordinates.setY(updatedCoordinates.getY());

        return coordinatesRepository.save(coordinates);
    }

    @Transactional
    public void replaceCoordinates(Long id, Long toReplaceId) {
        if(Objects.equals(id, toReplaceId)) {
            return ;
        }
        movieRepository.replaceCoordinates(id, toReplaceId);
        coordinatesRepository.deleteById(id);
    }

    public List<Coordinates> getAll(int pageNumber, String sortByField, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));
        Page<Coordinates> coordinatesPage = coordinatesRepository.findAll(pageRequest);
        return coordinatesPage.getContent();
    }
}

