package lab4.database.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lab4.database.entity.enums.MovieGenre;
import lab4.database.entity.enums.MpaaRating;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Coordinates coordinates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime creationDate = ZonedDateTime.now();

    private int oscarsCount;

    private float budget;

    private float totalBoxOffice;

    @Enumerated(EnumType.STRING)
    private MpaaRating mpaaRating;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Person director;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Person screenwriter;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Person operator;

    @Column(nullable = false)
    private Long length;

    private long goldenPalmCount;

    private String tagline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieGenre genre;

    @Column(nullable = false)
    private Boolean allowAdminEdit;

    @Column(nullable = false)
    private long creatorId;

    // Getters, Setters, and Constructors
}
