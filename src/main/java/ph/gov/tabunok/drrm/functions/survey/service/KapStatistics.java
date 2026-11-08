package ph.gov.tabunok.drrm.functions.survey.service;

import java.util.Collection;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;

/**
 * Statistical treatment described in the study: Pearson r for H01,
 * independent samples t-test / one-way ANOVA for H02. Methods return
 * {@code null} when the data cannot support the test.
 */
public final class KapStatistics {

    public static final double ALPHA = 0.05;

    private KapStatistics() {
    }

    public record TestResult(double statistic, double pValue) {
    }

    /** Pearson product-moment correlation with a two-tailed p-value. */
    public static TestResult pearson(double[] x, double[] y) {
        int n = x.length;
        if (n < 3 || y.length != n) return null;
        double r = new PearsonsCorrelation().correlation(x, y);
        if (Double.isNaN(r)) return null;
        if (Math.abs(r) >= 1.0) return new TestResult(Math.signum(r), 0.0);
        double t = r * Math.sqrt((n - 2) / (1 - r * r));
        double p = 2 * (1 - new TDistribution(n - 2).cumulativeProbability(Math.abs(t)));
        return new TestResult(r, p);
    }

    /** Independent samples t-test assuming equal variances. */
    public static TestResult tTest(double[] a, double[] b) {
        if (a.length < 2 || b.length < 2) return null;
        if (sd(a) == 0 && sd(b) == 0) return null;
        TTest test = new TTest();
        double t = test.homoscedasticT(a, b);
        double p = test.homoscedasticTTest(a, b);
        if (Double.isNaN(t) || Double.isNaN(p)) return null;
        return new TestResult(t, p);
    }

    /** One-way analysis of variance. Every group must have at least two observations. */
    public static TestResult anova(Collection<double[]> groups) {
        if (groups.size() < 2 || groups.stream().anyMatch(g -> g.length < 2)) return null;
        if (groups.stream().allMatch(g -> sd(g) == 0)) return null;
        OneWayAnova anova = new OneWayAnova();
        double f = anova.anovaFValue(groups);
        double p = anova.anovaPValue(groups);
        if (Double.isNaN(f) || Double.isNaN(p)) return null;
        return new TestResult(f, p);
    }

    public static double mean(double[] v) {
        if (v.length == 0) return 0;
        double s = 0;
        for (double d : v) s += d;
        return s / v.length;
    }

    /** Sample standard deviation (n - 1). */
    public static double sd(double[] v) {
        if (v.length < 2) return 0;
        return new StandardDeviation(true).evaluate(v);
    }

    public static String strength(double r) {
        double a = Math.abs(r);
        String dir = r < 0 ? "negative" : "positive";
        if (a < 0.20) return "Very weak " + dir;
        if (a < 0.40) return "Weak " + dir;
        if (a < 0.60) return "Moderate " + dir;
        if (a < 0.80) return "Strong " + dir;
        return "Very strong " + dir;
    }

    public static String decision(double p) {
        return p < ALPHA ? "Reject H\u2080 \u2013 significant" : "Fail to reject H\u2080 \u2013 not significant";
    }
}