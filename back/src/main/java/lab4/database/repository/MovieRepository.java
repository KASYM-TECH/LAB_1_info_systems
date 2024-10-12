package lab4.database.repository;

import jakarta.persistence.Tuple;
import lab4.database.entity.GroupByTotalBoxOffice;
import lab4.database.entity.Movie;
import lab4.database.entity.enums.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    @Modifying
    @Query(value = "UPDATE movie SET director_id=:replace_id WHERE director_id=:id", nativeQuery = true)
    void replaceDirector(@Param("id") Long id, @Param("replace_id") Long replaceId);

    @Modifying
    @Query(value = "UPDATE movie SET operator_id=:replace_id WHERE operator_id=:id", nativeQuery = true)
    void replaceOperator(@Param("id") Long id, @Param("replace_id") Long replaceId);

    @Modifying
    @Query(value = "UPDATE movie SET screenwriter_id=:replace_id WHERE screenwriter_id=:id", nativeQuery = true)
    void replaceScreenwriter(@Param("id") Long id, @Param("replace_id") Long replaceId);

    @Modifying
    @Query(value = "UPDATE movie SET coordinates_id=:replace_id WHERE coordinates_id=:id", nativeQuery = true)
    void replaceCoordinates(@Param("id") Long id, @Param("replace_id") Long replaceId);

    @Query(value = "SELECT get_movie_id_with_min_total_box_office()", nativeQuery = true)
    Long giveMinTotalBoxOffice();

    @Query(value = "SELECT total_box_office as totalBoxOffice, count FROM group_by_total_box_office()", nativeQuery = true)
    List<Tuple> groupByTotalBoxOffice();

    @Query(value = "select 1 from zero_oscar_count_by_genre(:genre)", nativeQuery = true)
    long zeroOscarCountByGenre(@Param("genre") String genre);

    @Query(value = "SELECT get_all_movies_with_no_oscars()", nativeQuery = true)
    List<Long> getAllWithNoOscars();
}
