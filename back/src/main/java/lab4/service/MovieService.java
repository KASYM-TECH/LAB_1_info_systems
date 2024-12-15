package lab4.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lab4.database.entity.*;
import lab4.database.entity.enums.MovieGenre;
import lab4.database.entity.enums.MpaaRating;
import lab4.database.entity.enums.Role;
import lab4.database.repository.CoordinatesRepository;
import lab4.database.repository.MovieRepository;
import lab4.database.repository.specifications.MovieSpecifications;
import lab4.database.repository.PersonRepository;
import lab4.exception.ForbiddenException;
import lab4.exception.UniqueConstraintException;
import lab4.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class MovieService {
    private MovieRepository movieRepository;
    private CoordinatesRepository coordinatesRepository;
    private PersonRepository personRepository;
    private PersonService personService;
    private CoordinatesService coordinatesService;
    private EntityManager entityManager;
    private IJwtService jwtService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Movie createMovie(Movie movie) throws UniqueConstraintException {
        try {

            var existingWithName = movieRepository.findByName(movie.getName());
            if (existingWithName != null) {
                throw new UniqueConstraintException();
            }

            movie.setCreationDate(ZonedDateTime.now());

            // Обработка поля Coordinates
            if (movie.getCoordinates() != null && movie.getCoordinates().getId() != 0) {
                Optional<Coordinates> existingCoordinates = coordinatesRepository.findById(movie.getCoordinates().getId());
                existingCoordinates.ifPresent(movie::setCoordinates);
            }

            // Обработка поля Director (Person)
            if (movie.getDirector() != null && movie.getDirector().getId() != 0) {
                Optional<Person> existingDirector = personRepository.findById(movie.getDirector().getId());
                existingDirector.ifPresent(movie::setDirector);
            }

            // Обработка поля Screenwriter (Person)
            if (movie.getScreenwriter() != null && movie.getScreenwriter().getId() != 0) {
                Optional<Person> existingScreenwriter = personRepository.findById(movie.getScreenwriter().getId());
                existingScreenwriter.ifPresent(movie::setScreenwriter);
            }

            // Обработка поля Operator (Person)
            if (movie.getOperator() != null && movie.getOperator().getId() != 0) {
                Optional<Person> existingOperator = personRepository.findById(movie.getOperator().getId());
                existingOperator.ifPresent(movie::setOperator);
            }

            entityManager.merge(movie.getCoordinates());
            entityManager.merge(movie.getDirector());
            entityManager.merge(movie.getScreenwriter());
            entityManager.merge(movie.getOperator());

            return movieRepository.save(movie);

        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException();
        }
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Movie updateMovie(Long id, Movie updatedMovie, String token) throws Exception {
        var existingWithName = movieRepository.findByName(updatedMovie.getName());
        if (existingWithName != null && !Objects.equals(existingWithName.getId(), id)) {
            throw new UniqueConstraintException();
        }

        var movie = movieRepository.findById(id).orElseThrow();
        var userId = jwtService.getUserIdFromToken(token);
        var role = jwtService.getUserRoleFromToken(token);

        if(movie.getCreatorId() != userId) {
            if(role != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!movie.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }
        movie.setName(updatedMovie.getName());
        movie.setOscarsCount(updatedMovie.getOscarsCount());
        movie.setBudget(updatedMovie.getBudget());
        movie.setTotalBoxOffice(updatedMovie.getTotalBoxOffice());
        movie.setMpaaRating(updatedMovie.getMpaaRating());
        movie.setGoldenPalmCount(updatedMovie.getGoldenPalmCount());
        movie.setTagline(updatedMovie.getTagline());
        movie.setGenre(updatedMovie.getGenre());
        movie.setLength(updatedMovie.getLength());

        try {
            if(updatedMovie.getCoordinates() != null && updatedMovie.getCoordinates().getId() != 0) {
                Coordinates updatedCoordinates = coordinatesService.updateCoordinates(movie.getCoordinates().getId(), updatedMovie.getCoordinates(), token);
                movie.setCoordinates(updatedCoordinates);
            }

            if(updatedMovie.getDirector() != null && updatedMovie.getDirector().getId() != 0) {
                Person updatedDirector = personService.updatePerson(movie.getDirector().getId(), updatedMovie.getDirector(), token);
                movie.setDirector(updatedDirector);
            }

            if(updatedMovie.getOperator() != null && updatedMovie.getOperator().getId() != 0) {
                Person updatedOperator = personService.updatePerson(movie.getOperator().getId(), updatedMovie.getOperator(), token);
                movie.setOperator(updatedOperator);
            }

            if (updatedMovie.getScreenwriter() != null) {
                Person updatedScreenwriter = personService.updatePerson(movie.getScreenwriter().getId(), updatedMovie.getScreenwriter(), token);
                movie.setScreenwriter(updatedScreenwriter);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public List<Movie> getAllMovies(int pageNumber, String sortByField, int pageSize, String name, String tagline, MovieGenre genre, MpaaRating rating) throws Exception {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<Movie> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(MovieSpecifications.nameContains(name));
        }

        if (tagline != null && !tagline.isEmpty()) {
            spec = spec.and(MovieSpecifications.taglineContains(tagline));
        }

        if (genre != null) {
            spec = spec.and(MovieSpecifications.genreContains(genre));
        }

        if (rating != null) {
            spec = spec.and(MovieSpecifications.mpaaRatingContains(rating));
        }

        return movieRepository.findAll(spec, pageRequest).getContent();
    }

    public long Count(){
        return movieRepository.count();
    }

    public Long giveMinTotalBoxOffice() {
        var id = movieRepository.giveMinTotalBoxOffice();
        if(id == null) {
            return 0L;
        }
        return id;
    }

    public List<GroupByTotalBoxOffice> groupByTotalBoxOffice() {
        var group = movieRepository.groupByTotalBoxOffice();
        var res = new LinkedList<GroupByTotalBoxOffice>();
        if(group == null) {
            return res;
        }
        for (Tuple tuple : group) {
            Float totalBoxOffice = tuple.get(0, Float.class); // Index or field name can be used
            Long count = tuple.get(1, Long.class);
            res.add(new GroupByTotalBoxOffice(totalBoxOffice, count));
        }
        return res;
    }

    public List<Movie> getAllWithNoOscars() {
        var movieIds = movieRepository.getAllWithNoOscars();
        if(movieIds == null) {
            return new LinkedList<>();
        }
        var movies = new ArrayList<Movie>();
        for(var movieId : movieIds) {
            movies.add(movieRepository.findById(movieId).orElseThrow());
        }
        return movies;
    }

    @Transactional
    public long zeroOscarCountByGenre(MovieGenre genre) {
         return movieRepository.zeroOscarCountByGenre(genre.toString());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int insertMovies(List<Movie> moviesList) {
        try {
            var count = 0;
            for (Movie movie : moviesList) {
                if (!movie.Validate()) {
                    return -1;
                }
                count++;
                if(movie.getDirector() != null) {
                    if (!movie.getDirector().Validate()) {
                        return -1;
                    }
                    personService.createPerson(movie.getDirector());
                    count++;
                    if(movie.getDirector().getLocation() != null) {
                        if (!movie.getDirector().getLocation().Validate()) {
                            return -1;
                        }
                        count++;
                    }
                }
                if(movie.getCoordinates() != null) {
                    if (!movie.getCoordinates().Validate()) {
                        return -1;
                    }
                    count++;
                }
                if(movie.getScreenwriter() != null) {
                    if (!movie.getScreenwriter().Validate()) {
                        return -1;
                    }
                    personService.createPerson(movie.getScreenwriter());
                    count++;
                    if(movie.getScreenwriter().getLocation() != null) {
                        if (!movie.getScreenwriter().getLocation().Validate()) {
                            return -1;
                        }
                        count++;
                    }
                }
                if(movie.getOperator() != null) {
                    if (!movie.getOperator().Validate()) {
                        return -1;
                    }
                    personService.createPerson(movie.getOperator());
                    count++;
                    if(movie.getOperator().getLocation() != null) {
                        if (!movie.getOperator().getLocation().Validate()) {
                            return -1;
                        }
                        count++;
                    }
                }
            }
            movieRepository.saveAll(moviesList);
            return count;
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Movie> uploadJsonFile(MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file.getInputStream(), new TypeReference<List<Movie>>() {});
        } catch (IOException e) {
            return null;
        }
    }
}

