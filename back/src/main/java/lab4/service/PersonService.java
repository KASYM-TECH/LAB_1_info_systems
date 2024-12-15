package lab4.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
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
import lab4.exception.UniqueConstraintException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person createPerson(Person person) throws UniqueConstraintException {
        try {
            var existingWithName = personRepository.findByName(person.getName());
            if (existingWithName != null) {
                throw new UniqueConstraintException();
            }
            // Check and set existing location if provided
            if (person.getLocation() != null && person.getLocation().getId() != 0) {
                Optional<Location> existing = locationRepository.findById(person.getLocation().getId());
                existing.ifPresent(person::setLocation);
                entityManager.merge(person.getLocation());
            }

            // Save person
            return personRepository.save(person);

        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException();
        }
    }

    public Person getPersonById(Long id) throws Exception {
        return personRepository.findById(id).orElseThrow(() -> new Exception("Person not found"));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Person updatePerson(Long id, Person updatedPerson, String token) throws Exception {
        var existingWithName = personRepository.findByName(updatedPerson.getName());
        if (existingWithName != null && !Objects.equals(existingWithName.getId(), id)) {
            throw new UniqueConstraintException();
        }

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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void replaceWith(Long id, Long replaceWithId) {
        if(Objects.equals(id, replaceWithId)) return;

        movieRepository.replaceOperator(id, replaceWithId);
        movieRepository.replaceDirector(id, replaceWithId);
        movieRepository.replaceScreenwriter(id, replaceWithId);

        personRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int insertPersons(List<Person> personList) {
        try {
            var count = 0;
            for (Person person : personList) {
                if (!person.Validate()) {
                    return -1;
                }
                if(person.getLocation() != null) {
                    if (!person.getLocation().Validate()) {
                        return -1;
                    }
                    count++;
                }
                createPerson(person);
                count++;
            }
            return count;
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Person> uploadJsonFile(MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file.getInputStream(), new TypeReference<List<Person>>() {});
        } catch (IOException e) {
            return null;
        }
    }
}
