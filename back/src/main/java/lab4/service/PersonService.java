package lab4.service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lab4.connection.controller.LocationController;
import lab4.database.entity.Coordinates;
import lab4.database.entity.Location;
import lab4.database.entity.Movie;
import lab4.database.entity.Person;
import lab4.database.entity.enums.Color;
import lab4.database.entity.enums.Country;
import lab4.database.entity.enums.MovieGenre;
import lab4.database.entity.enums.Role;
import lab4.database.repository.LocationRepository;
import lab4.database.repository.MovieRepository;
import lab4.database.repository.PersonRepository;
import lab4.database.repository.specifications.MovieSpecifications;
import lab4.database.repository.specifications.PersonSpecifications;
import lab4.exception.ForbiddenException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
public class PersonService {
    private PersonRepository personRepository;
    private MovieRepository movieRepository;
    private LocationRepository locationRepository;
    private IJwtService jwtService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Person createPerson(Person person) {
        if (person.getLocation() != null && person.getLocation().getId() != 0) {
            Optional<Location> existing = locationRepository.findById(person.getLocation().getId());
            existing.ifPresent(person::setLocation);
        }

        entityManager.merge(person.getLocation());

        return personRepository.save(person);
    }

    public Person getPersonById(Long id) throws Exception {
        return personRepository.findById(id).orElseThrow(() -> new Exception("Person not found"));
    }

    @Transactional
    public Person updatePerson(Long id, Person updatedPerson, String token) throws Exception {
        var person = personRepository.findById(id).orElseThrow();
        var userRole = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        if(person.getCreatorId() != userId) {
            if(userRole != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!person.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }

        person.setName(updatedPerson.getName());
        person.setEyeColor(updatedPerson.getEyeColor());
        person.setHairColor(updatedPerson.getHairColor());
        person.setLocation(updatedPerson.getLocation());
        person.setHeight(updatedPerson.getHeight());
        person.setNationality(updatedPerson.getNationality());

        return personRepository.save(person);
    }

    public List<Person> getAll(int pageNumber, String sortByField, int pageSize,
                               String name, Color eyeColor, Color hairColor, Country nationality) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<Person> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(PersonSpecifications.nameContains(name));
        }

        if (eyeColor != null) {
            spec = spec.and(PersonSpecifications.eyeColorIs(eyeColor));
        }

        if (hairColor != null) {
            spec = spec.and(PersonSpecifications.hairColorIs(hairColor));
        }

        if (nationality != null) {
            spec = spec.and(PersonSpecifications.nationalityIs(nationality));
        }

        Page<Person> coordinatesPage = personRepository.findAll(spec, pageRequest);
        return coordinatesPage.getContent();
    }

    @Transactional
    public void replaceWith(Long id, Long replaceWithId) {
        if(Objects.equals(id, replaceWithId)) return;

        movieRepository.replaceOperator(id, replaceWithId);
        movieRepository.replaceDirector(id, replaceWithId);
        movieRepository.replaceScreenwriter(id, replaceWithId);

        personRepository.deleteById(id);
    }
}
