package ph.gov.tabunok.drrm.functions.survey.service;

import ph.gov.tabunok.drrm.functions.survey.dto.DomainScore;

/**
 * Interpretation of the 4-point Likert scale used by the questionnaire.
 * <pre>
 * 3.26 - 4.00  Strongly Agree     Very High
 * 2.51 - 3.25  Agree              High
 * 1.76 - 2.50  Disagree           Low
 * 1.00 - 1.75  Strongly Disagree  Very Low
 * </pre>
 */
public final class Scale {

    private Scale() {
    }

    public static String level(double mean) {
        double m = round2(mean);
        if (m >= 3.26) return "Very High";
        if (m >= 2.51) return "High";
        if (m >= 1.76) return "Low";
        return "Very Low";
    }

    public static String agreement(double mean) {
        double m = round2(mean);
        if (m >= 3.26) return "Strongly Agree";
        if (m >= 2.51) return "Agree";
        if (m >= 1.76) return "Disagree";
        return "Strongly Disagree";
    }

    public static int percent(double mean) {
        long p = Math.round((mean - 1) / 3.0 * 100);
        return (int) Math.max(0, Math.min(100, p));
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    public static int reverse(int raw) {
        return 5 - raw;
    }

    public static DomainScore score(String key, String label, double mean) {
        return DomainScore.builder()
                .key(key)
                .label(label)
                .mean(round2(mean))
                .percent(percent(mean))
                .level(level(mean))
                .build();
    }
}