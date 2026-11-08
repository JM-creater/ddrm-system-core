package ph.gov.tabunok.drrm.functions.guide.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;

/** Preparedness guide for one hazard. Before / during / after are stored one step per line. */
@Entity
@Table(name = "guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentType hazard;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String beforeSteps;

    @Column(columnDefinition = "TEXT")
    private String duringSteps;

    @Column(columnDefinition = "TEXT")
    private String afterSteps;

    private int sortOrder;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
