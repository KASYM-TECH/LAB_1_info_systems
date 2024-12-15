package lab4.database.repository;

import lab4.database.entity.ImportHistory;
import lab4.database.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long>, JpaSpecificationExecutor<ImportHistory> {
}
