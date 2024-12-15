package lab4.database.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Long y;

    @Column(nullable = false)
    private Integer z;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean allowAdminEdit;

    @Column(nullable = false)
    private long creatorId;

    public boolean Validate() {
        return true;
    }
}