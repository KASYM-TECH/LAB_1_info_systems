package lab4.service;

import lab4.database.entity.Coordinates;
import lab4.database.entity.Location;
import lab4.database.entity.Person;
import lab4.database.entity.enums.Color;
import lab4.database.entity.enums.Country;
import lab4.database.entity.enums.Role;
import lab4.database.repository.LocationRepository;
import lab4.database.repository.PersonRepository;
import lab4.database.repository.specifications.LocationSpecifications;
import lab4.database.repository.specifications.PersonSpecifications;
import lab4.exception.ForbiddenException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LocationService {
    private LocationRepository locationRepository;
    private PersonRepository personRepository;
    private IJwtService jwtService;

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) throws Exception {
        return locationRepository.findById(id).orElseThrow(() -> new Exception("Location not found"));
    }

    @Transactional
    public Location updateLocation(Long id, Location updatedLocation, String token) throws Exception {
        var location = locationRepository.findById(id).orElseThrow();
        var userRole = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        if(location.getCreatorId() != userId) {
            if(userRole != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!location.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }

        location.setX(updatedLocation.getX());
        location.setY(updatedLocation.getY());
        location.setZ(updatedLocation.getZ());
        location.setName(updatedLocation.getName());

        return locationRepository.save(location);
    }

    @Transactional
    public void replaceWith(Long id, Long replaceWithId) {
        if(Objects.equals(id, replaceWithId)) {
            return ;
        }
        personRepository.replaceLocation(id, replaceWithId);
        locationRepository.deleteById(id);
    }

    public List<Location> getAll(int pageNumber, String sortByField, int pageSize, String name) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<Location> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(LocationSpecifications.hasName(name));
        }

        Page<Location> coordinatesPage = locationRepository.findAll(spec, pageRequest);
        return coordinatesPage.getContent();
    }
}

