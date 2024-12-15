package lab4.service;

import lab4.database.entity.*;
import lab4.database.entity.enums.Role;
import lab4.database.repository.ImportHistoryRepository;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ImportHistoryService {

    private ImportHistoryRepository ihRepository;
    private CoordinatesService coordinatesService;
    private LocationService locationService;
    private MovieService movieService;
    private PersonService personService;

    private IJwtService jwtService;

    @Transactional
    public ImportHistory createImportHistory(String token, String entityType, MultipartFile multipartFile) {
        var ih = new ImportHistory();
        ih.setUserId(jwtService.getUserIdFromToken(token));
        ih.setStatus("SUCCESS");
        ih.setAddedObjects(0);

        switch (entityType) {
            case "movie":
                List<Movie> movies = movieService.uploadJsonFile(multipartFile);
                if(movies == null) {
                    ih.setStatus("FAILED");
                    break;
                }
                ih.setAddedObjects(movieService.insertMovies(movies));
                break;
            case "location":
                List<Location> locations = locationService.uploadJsonFile(multipartFile);
                if(locations == null) {
                    ih.setStatus("FAILED");
                    break;
                }
                ih.setAddedObjects(locationService.insertLocations(locations));
                break;
            case "coordinates":
                List<Coordinates> coordinates = coordinatesService.uploadJsonFile(multipartFile);
                if(coordinates == null) {
                    ih.setStatus("FAILED");
                    break;
                }
                ih.setAddedObjects(coordinatesService.insertCoordinates(coordinates));
                break;
            case "person":
                List<Person> persons = personService.uploadJsonFile(multipartFile);
                if(persons == null) {
                    ih.setStatus("FAILED");
                    break;
                }
                ih.setAddedObjects(personService.insertPersons(persons));
                break;
        }

        if(ih.getAddedObjects() == -1) {
            ih.setStatus("FAILED");
            ih.setAddedObjects(0);
        }

        ih.setTimestamp(LocalDateTime.now());
        return ihRepository.save(ih);
    }

    public List<ImportHistory> getAll(int pageNumber, String sortByField, int pageSize, String token) {
        var role = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<ImportHistory> spec = Specification.where(null);

        if (role != Role.Admin) {
            spec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userId"), userId);
        }

        Page<ImportHistory> importHistoryPage = ihRepository.findAll(spec, pageRequest);
        return importHistoryPage.getContent();
    }
}
