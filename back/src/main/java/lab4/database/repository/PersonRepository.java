package lab4.database.repository;

import lab4.database.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Modifying
    @Query(value = "UPDATE person SET location_id=:replace_id WHERE location_id=:id", nativeQuery = true)
    void replaceLocation(@Param("id") Long id, @Param("replace_id") Long replaceId);
}

