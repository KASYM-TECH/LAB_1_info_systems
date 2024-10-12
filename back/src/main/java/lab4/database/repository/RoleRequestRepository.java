package lab4.database.repository;

import lab4.database.entity.RoleRequest;
import lab4.database.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    @Query(value = "SELECT rr FROM RoleRequest rr WHERE rr.role = :role")
    List<RoleRequest> getByRole(@Param("role") Role role);

    @Query(value = "UPDATE rolerequest SET fulfilled=true WHERE id=:role_request_id RETURNING role", nativeQuery = true)
    Role fulfill(@Param("role_request_id" ) Long roleRequestId);
}

