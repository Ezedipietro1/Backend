package utnfc.isi.back.domain;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DESIGNERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Designer {
    @Id
    @SequenceGenerator(name = "seq_designer_id", sequenceName = "SEQ_DESIGNER_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_designer_id")
    @Column(name = "ID_DESIGNER")
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 160)
    private String nombre;

    @OneToMany(mappedBy = "designer", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<BoardGame> boardGames = new HashSet<>();
}
