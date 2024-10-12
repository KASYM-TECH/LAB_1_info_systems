package lab4.database.repository.specifications;

import lab4.database.entity.Person;
import lab4.database.entity.enums.Color;
import lab4.database.entity.enums.Country;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecifications {
    public static Specification<Person> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Person> eyeColorIs(Color eyeColor) {
        return (root, query, criteriaBuilder) -> {
            if (eyeColor == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("eyeColor"), eyeColor);
        };
    }

    public static Specification<Person> hairColorIs(Color hairColor) {
        return (root, query, criteriaBuilder) -> {
            if (hairColor == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("hairColor"), hairColor);
        };
    }

    public static Specification<Person> nationalityIs(Country nationality) {
        return (root, query, criteriaBuilder) -> {
            if (nationality == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("nationality"), nationality);
        };
    }
}
