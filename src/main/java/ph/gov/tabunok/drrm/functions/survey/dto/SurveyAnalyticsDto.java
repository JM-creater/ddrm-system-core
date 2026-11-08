package ph.gov.tabunok.drrm.functions.survey.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnalyticsDto {

    private int respondents;
    private List<ProfileTable> profile;
    private List<DomainResult> domains;
    private DomainScore overall;
    private List<CorrelationResult> correlations;
    private List<DifferenceResult> differences;
    private List<ItemResult> gaps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileTable {
        private String variable;
        private List<ProfileRow> rows;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileRow {
        private String label;
        private int frequency;
        private double percent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DomainResult {
        private String key;
        private String label;
        private double mean;
        private double sd;
        private String level;
        private List<ItemResult> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemResult {
        private String section;
        private int itemNo;
        private String statement;
        private String indicator;
        private boolean negative;
        private double mean;
        private String description;
        private String level;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorrelationResult {
        private String variables;
        private Double r;
        private Double p;
        private String strength;
        private String decision;
        private String note;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifferenceResult {
        private String variable;
        private String test;
        private Double statistic;
        private Double p;
        private String decision;
        private String note;
        private List<GroupMean> groups;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMean {
        private String label;
        private int n;
        private double mean;
    }
}