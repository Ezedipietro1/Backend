package utnfc.isi.back.domain;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @SequenceGenerator(name = "seq_category_id", sequenceName = "SEQ_CATEGORY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_category_id")
    @Column(name = "ID_CATEGORY")
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 160)
    private String nombre;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<BoardGame> boardGames = new HashSet<>();
}
