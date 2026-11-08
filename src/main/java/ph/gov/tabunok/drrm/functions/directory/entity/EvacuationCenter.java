package ph.gov.tabunok.drrm.functions.directory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evacuation_centers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvacuationCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 200)
    private String location;

    /** Puroks assigned to this center, free text. */
    @Column(length = 150)
    private String puroksServed;

    private Integer capacity;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 30)
    private String contactNumber;

    @Builder.Default
    @Column(name = "is_open", nullable = false)
    private boolean open = false;
}
