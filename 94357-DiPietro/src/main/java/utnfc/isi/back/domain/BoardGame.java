package utnfc.isi.back.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "BOARD_GAMES",
    indexes = {
        @Index(name = "IX_BG_NAME", columnList = "NAME"),
        @Index(name = "IX_BG_CATEGORY", columnList = "ID_CATEGORY"),
        @Index(name = "IX_BG_PUBLISHER", columnList = "ID_PUBLISHER"),
        @Index(name = "IX_BG_DESIGNER", columnList = "ID_DESIGNER"),
        @Index(name = "IX_BG_RATING", columnList = "AVERAGE_RATING"),
        @Index(name = "IX_BG_YEAR", columnList = "YEAR_PUBLISHED")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_board_game_id")
    @SequenceGenerator(
        name = "seq_board_game_id",
        sequenceName = "SEQ_BOARD_GAME_ID",
        allocationSize = 1
    )
    @Column(name = "ID_GAME")
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "YEAR_PUBLISHED")
    private Integer yearPublished;

    @Column(name = "MIN_AGE")
    private Integer minAge;

    @Column(name = "AVERAGE_RATING", precision = 3, scale = 2)
    private Double averageRating;

    @Column(name = "USERS_RATING")
    private Integer usersRating;

    @Column(name = "MIN_PLAYERS")
    private Integer minPlayers;

    @Column(name = "MAX_PLAYERS")
    private Integer maxPlayers;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_DESIGNER", nullable = false,
                foreignKey = @ForeignKey(name = "FK_BG_DESIGNER"))
    private Designer designer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PUBLISHER", nullable = false,
                foreignKey = @ForeignKey(name = "FK_BG_PUBLISHER"))
    private Publisher publisher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CATEGORY", nullable = false,
                foreignKey = @ForeignKey(name = "FK_BG_CATEGORY"))
    private Category category;
}
