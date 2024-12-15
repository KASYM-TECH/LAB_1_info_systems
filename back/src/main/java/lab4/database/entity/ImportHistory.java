package lab4.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // SUCCESS or FAILED

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Integer addedObjects;

    public ImportHistory() {
    }

    public ImportHistory(String status, Long userId, Integer addedObjects) {
        this.status = status;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
        this.addedObjects = addedObjects;
    }
}