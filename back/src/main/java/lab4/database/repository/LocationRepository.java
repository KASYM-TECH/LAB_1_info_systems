package lab4.database.repository;

import lab4.database.entity.Location;
import lab4.database.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
}

