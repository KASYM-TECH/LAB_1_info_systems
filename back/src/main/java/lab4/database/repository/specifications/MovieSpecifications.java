package lab4.database.repository.specifications;

import lab4.database.entity.Movie;
import lab4.database.entity.enums.MovieGenre;
import lab4.database.entity.enums.MpaaRating;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {
    public static Specification<Movie> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Movie> taglineContains(String tagline) {
        return (root, query, criteriaBuilder) -> {
            if (tagline == null || tagline.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("tagline")), "%" + tagline.toLowerCase() + "%");
        };
    }

    public static Specification<Movie> genreContains(MovieGenre genre) {
        return (root, query, criteriaBuilder) -> {
            if (genre == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("genre"), genre);
        };
    }

    public static Specification<Movie> mpaaRatingContains(MpaaRating rating) {
        return (root, query, criteriaBuilder) -> {
            if (rating == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("mpaaRating"), rating);
        };
    }
}

