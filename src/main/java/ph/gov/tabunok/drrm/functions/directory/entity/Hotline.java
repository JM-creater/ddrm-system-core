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
@Table(name = "hotlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String agency;

    @Column(nullable = false, length = 40)
    private String number;

    @Column(length = 200)
    private String description;

    private int sortOrder;
}
