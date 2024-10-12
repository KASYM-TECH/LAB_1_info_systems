package lab4.database.repository;

import lab4.database.entity.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {

}

