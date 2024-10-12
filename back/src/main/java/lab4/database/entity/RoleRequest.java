package lab4.database.entity;


import jakarta.persistence.*;
import lab4.database.entity.enums.Role;
import lombok.Data;

@Entity
@Data
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean fulfilled;
}
