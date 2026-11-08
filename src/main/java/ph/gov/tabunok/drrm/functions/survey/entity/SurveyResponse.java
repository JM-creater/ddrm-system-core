package ph.gov.tabunok.drrm.functions.survey.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.gov.tabunok.drrm.functions.user.entity.User;

/**
 * A resident's answered KAP questionnaire. Each resident keeps one response;
 * answering again replaces the previous answers.
 */
@Entity
@Table(name = "survey_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Part I - respondent's profile
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Education education;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HouseholdSize householdSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResidencyLength residency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncomeBracket income;

    @Column(nullable = false)
    private boolean experiencedDisaster;

    // Weighted means after reverse coding (1.00 - 4.00)
    private double knowledgeMean;
    private double attitudeMean;
    private double practiceMean;
    private double overallMean;

    @Builder.Default
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyAnswer> answers = new ArrayList<>();

    private LocalDateTime submittedAt;
}
