package ph.gov.tabunok.drrm.functions.survey.entity;

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

/** One statement of the KAP questionnaire (Parts II to IV of the research instrument). */
@Entity
@Table(name = "survey_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveySection section;

    @Column(nullable = false)
    private int itemNo;

    @Column(nullable = false, length = 300)
    private String statement;

    /** Negatively worded statements are reverse coded (4 becomes 1, 3 becomes 2 ...). */
    @Column(nullable = false)
    private boolean negative;

    /** Sub-dimension measured by the item, e.g. "Early warning systems". */
    @Column(length = 80)
    private String indicator;
}
