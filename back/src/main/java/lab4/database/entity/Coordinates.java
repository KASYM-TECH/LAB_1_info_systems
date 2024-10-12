package lab4.database.entity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Double y;

    @Column(nullable = false)
    private Boolean allowAdminEdit;

    @Column(nullable = false)
    private long creatorId;
}