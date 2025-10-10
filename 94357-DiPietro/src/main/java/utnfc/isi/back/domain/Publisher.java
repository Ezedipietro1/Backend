package utnfc.isi.back.domain;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PUBLISHERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {
    @Id
    @SequenceGenerator(name = "seq_publisher_id", sequenceName = "SEQ_PUBLISHER_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_publisher_id")
    @Column(name = "ID_PUBLISHER")
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 160)
    private String nombre;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<BoardGame> boardGames = new HashSet<>();
}
