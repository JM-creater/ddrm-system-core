package ph.gov.tabunok.drrm.functions.survey.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;
import ph.gov.tabunok.drrm.functions.incident.service.Csv;
import ph.gov.tabunok.drrm.functions.quiz.dto.OptionDto;
import ph.gov.tabunok.drrm.functions.survey.dto.MySurveyDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.CorrelationResult;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.DifferenceResult;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.DomainResult;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.GroupMean;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.ItemResult;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.ProfileRow;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyAnalyticsDto.ProfileTable;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyFormDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveyQuestionDto;
import ph.gov.tabunok.drrm.functions.survey.dto.SurveySubmitRequest;
import ph.gov.tabunok.drrm.functions.survey.entity.AgeGroup;
import ph.gov.tabunok.drrm.functions.survey.entity.Education;
import ph.gov.tabunok.drrm.functions.survey.entity.HouseholdSize;
import ph.gov.tabunok.drrm.functions.survey.entity.IncomeBracket;
import ph.gov.tabunok.drrm.functions.survey.entity.ResidencyLength;
import ph.gov.tabunok.drrm.functions.survey.entity.Sex;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveyAnswer;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveyQuestion;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveyResponse;
import ph.gov.tabunok.drrm.functions.survey.entity.SurveySection;
import ph.gov.tabunok.drrm.functions.survey.repository.SurveyQuestionRepository;
import ph.gov.tabunok.drrm.functions.survey.repository.SurveyResponseRepository;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private static final Map<SurveySection, String> SHORT_LABEL = Map.of(
            SurveySection.KNOWLEDGE, "Knowledge",
            SurveySection.ATTITUDE, "Attitudes",
            SurveySection.PRACTICE, "Practices");

    private final SurveyQuestionRepository questionRepository;
    private final SurveyResponseRepository responseRepository;
    private final UserRepository userRepository;

    // ------------------------------------------------------------------ form

    @Override
    @Transactional(readOnly = true)
    public SurveyFormDto form() {
        Map<String, List<OptionDto>> options = new LinkedHashMap<>();
        options.put("ageGroup", options(AgeGroup.values()));
        options.put("sex", options(Sex.values()));
        options.put("education", options(Education.values()));
        options.put("householdSize", options(HouseholdSize.values()));
        options.put("residency", options(ResidencyLength.values()));
        options.put("income", options(IncomeBracket.values()));
        options.put("experiencedDisaster", List.of(new OptionDto("true", "Yes"), new OptionDto("false", "No")));
        return SurveyFormDto.builder()
                .questions(orderedQuestions().stream().map(q -> SurveyQuestionDto.builder()
                        .id(q.getId())
                        .section(q.getSection())
                        .sectionLabel(q.getSection().getLabel())
                        .itemNo(q.getItemNo())
                        .statement(q.getStatement())
                        .indicator(q.getIndicator())
                        .build()).toList())
                .profileOptions(options)
                .scale(List.of(new OptionDto("4", "Strongly Agree"), new OptionDto("3", "Agree"),
                        new OptionDto("2", "Disagree"), new OptionDto("1", "Strongly Disagree")))
                .build();
    }

    // ---------------------------------------------------------------- submit

    @Override
    @Transactional
    public MySurveyDto submit(Long userId, SurveySubmitRequest req) {
        List<SurveyQuestion> questions = orderedQuestions();
        Map<Long, Integer> answers = req.getAnswers();
        for (SurveyQuestion q : questions) {
            Integer v = answers.get(q.getId());
            if (v == null) {
                throw ApiException.badRequest("Please answer statement " + q.getItemNo() + " in Part "
                        + partNo(q.getSection()) + ".");
            }
            if (v < 1 || v > 4) throw ApiException.badRequest("Answers must be from 1 to 4.");
        }
        if (answers.keySet().stream().anyMatch(id -> questions.stream().noneMatch(q -> q.getId().equals(id)))) {
            throw ApiException.badRequest("The questionnaire has changed. Please reload the page.");
        }

        SurveyResponse r = responseRepository.findByUserId(userId).orElseGet(() -> SurveyResponse.builder()
                .user(userRepository.getReferenceById(userId)).build());
        r.setAgeGroup(req.getAgeGroup());
        r.setSex(req.getSex());
        r.setEducation(req.getEducation());
        r.setHouseholdSize(req.getHouseholdSize());
        r.setResidency(req.getResidency());
        r.setIncome(req.getIncome());
        r.setExperiencedDisaster(req.getExperiencedDisaster());
        r.getAnswers().clear();

        Map<SurveySection, double[]> sums = new EnumMap<>(SurveySection.class); // [sum, count]
        double total = 0;
        for (SurveyQuestion q : questions) {
            int raw = answers.get(q.getId());
            int scored = q.isNegative() ? Scale.reverse(raw) : raw;
            r.getAnswers().add(SurveyAnswer.builder().response(r).question(q).rawValue(raw).scoredValue(scored).build());
            double[] s = sums.computeIfAbsent(q.getSection(), k -> new double[2]);
            s[0] += scored;
            s[1]++;
            total += scored;
        }
        r.setKnowledgeMean(avg(sums.get(SurveySection.KNOWLEDGE)));
        r.setAttitudeMean(avg(sums.get(SurveySection.ATTITUDE)));
        r.setPracticeMean(avg(sums.get(SurveySection.PRACTICE)));
        r.setOverallMean(questions.isEmpty() ? 0 : total / questions.size());
        r.setSubmittedAt(LocalDateTime.now());
        responseRepository.save(r);
        return toMine(r);
    }

    // -------------------------------------------------------------- my result

    @Override
    @Transactional(readOnly = true)
    public MySurveyDto mine(Long userId) {
        return responseRepository.findByUserId(userId).map(this::toMine)
                .orElseGet(() -> MySurveyDto.builder().submitted(false).build());
    }

    @Override
    @Transactional(readOnly = true)
    public MySurveyDto mineOrNull(Long userId) {
        return responseRepository.findByUserId(userId).map(this::toMine).orElse(null);
    }

    private MySurveyDto toMine(SurveyResponse r) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("ageGroup", r.getAgeGroup().name());
        profile.put("sex", r.getSex().name());
        profile.put("education", r.getEducation().name());
        profile.put("householdSize", r.getHouseholdSize().name());
        profile.put("residency", r.getResidency().name());
        profile.put("income", r.getIncome().name());
        profile.put("experiencedDisaster", String.valueOf(r.isExperiencedDisaster()));

        Map<Long, Integer> answers = new HashMap<>();
        List<String> focus = new ArrayList<>();
        r.getAnswers().stream()
                .sorted(Comparator.comparing((SurveyAnswer a) -> a.getQuestion().getSection())
                        .thenComparing(a -> a.getQuestion().getItemNo()))
                .forEach(a -> {
                    answers.put(a.getQuestion().getId(), a.getRawValue());
                    if (a.getScoredValue() <= 2) focus.add(a.getQuestion().getStatement());
                });

        return MySurveyDto.builder()
                .submitted(true)
                .submittedAt(r.getSubmittedAt())
                .profile(profile)
                .answers(answers)
                .domains(List.of(
                        Scale.score("KNOWLEDGE", "Knowledge", r.getKnowledgeMean()),
                        Scale.score("ATTITUDE", "Attitudes", r.getAttitudeMean()),
                        Scale.score("PRACTICE", "Practices", r.getPracticeMean())))
                .overall(Scale.score("OVERALL", "Overall readiness", r.getOverallMean()))
                .focusAreas(focus)
                .build();
    }

    // -------------------------------------------------------------- analytics

    @Override
    @Transactional(readOnly = true)
    public SurveyAnalyticsDto analytics() {
        List<SurveyResponse> rs = responseRepository.findAllWithAnswers();
        List<SurveyQuestion> questions = orderedQuestions();
        int n = rs.size();

        List<ProfileTable> profile = List.of(
                profileTable("Age", rs, SurveyResponse::getAgeGroup, AgeGroup.values()),
                profileTable("Sex", rs, SurveyResponse::getSex, Sex.values()),
                profileTable("Educational attainment", rs, SurveyResponse::getEducation, Education.values()),
                profileTable("Household size", rs, SurveyResponse::getHouseholdSize, HouseholdSize.values()),
                profileTable("Length of residency", rs, SurveyResponse::getResidency, ResidencyLength.values()),
                profileTable("Monthly household income", rs, SurveyResponse::getIncome, IncomeBracket.values()),
                yesNoTable("Experienced a disaster before", rs));

        if (n == 0) {
            return SurveyAnalyticsDto.builder().respondents(0).profile(profile)
                    .domains(List.of()).correlations(List.of()).differences(List.of()).gaps(List.of()).build();
        }

        // item means
        Map<Long, double[]> itemSums = new HashMap<>(); // [scoredSum, rawSum, count]
        for (SurveyResponse r : rs) {
            for (SurveyAnswer a : r.getAnswers()) {
                double[] s = itemSums.computeIfAbsent(a.getQuestion().getId(), k -> new double[3]);
                s[0] += a.getScoredValue();
                s[1] += a.getRawValue();
                s[2]++;
            }
        }
        List<ItemResult> allItems = new ArrayList<>();
        Map<SurveySection, List<ItemResult>> bySection = new EnumMap<>(SurveySection.class);
        for (SurveyQuestion q : questions) {
            double[] s = itemSums.get(q.getId());
            if (s == null || s[2] == 0) continue;
            double scored = s[0] / s[2];
            double raw = s[1] / s[2];
            ItemResult item = ItemResult.builder()
                    .section(SHORT_LABEL.get(q.getSection()))
                    .itemNo(q.getItemNo())
                    .statement(q.getStatement())
                    .indicator(q.getIndicator())
                    .negative(q.isNegative())
                    .mean(Scale.round2(scored))
                    .description(Scale.agreement(raw))
                    .level(Scale.level(scored))
                    .build();
            allItems.add(item);
            bySection.computeIfAbsent(q.getSection(), k -> new ArrayList<>()).add(item);
        }

        double[] k = values(rs, SurveyResponse::getKnowledgeMean);
        double[] a = values(rs, SurveyResponse::getAttitudeMean);
        double[] p = values(rs, SurveyResponse::getPracticeMean);
        double[] overall = values(rs, SurveyResponse::getOverallMean);

        List<DomainResult> domains = List.of(
                domain(SurveySection.KNOWLEDGE, k, bySection),
                domain(SurveySection.ATTITUDE, a, bySection),
                domain(SurveySection.PRACTICE, p, bySection));

        List<CorrelationResult> correlations = List.of(
                correlation("Knowledge and Attitudes", k, a),
                correlation("Knowledge and Practices", k, p),
                correlation("Attitudes and Practices", a, p));

        List<DifferenceResult> differences = List.of(
                difference("Age", rs, r -> r.getAgeGroup().getLabel(), labels(AgeGroup.values())),
                difference("Sex", rs, r -> r.getSex().getLabel(), labels(Sex.values())),
                difference("Educational attainment", rs, r -> r.getEducation().getLabel(), labels(Education.values())),
                difference("Household size", rs, r -> r.getHouseholdSize().getLabel(), labels(HouseholdSize.values())),
                difference("Length of residency", rs, r -> r.getResidency().getLabel(), labels(ResidencyLength.values())),
                difference("Monthly household income", rs, r -> r.getIncome().getLabel(), labels(IncomeBracket.values())),
                difference("Experience of past disasters", rs, r -> r.isExperiencedDisaster() ? "Yes" : "No",
                        List.of("Yes", "No")));

        List<ItemResult> gaps = allItems.stream()
                .sorted(Comparator.comparingDouble(ItemResult::getMean))
                .limit(5)
                .toList();

        return SurveyAnalyticsDto.builder()
                .respondents(n)
                .profile(profile)
                .domains(domains)
                .overall(Scale.score("OVERALL", "Overall disaster readiness", KapStatistics.mean(overall)))
                .correlations(correlations)
                .differences(differences)
                .gaps(gaps)
                .build();
    }

    // ----------------------------------------------------------------- export

    @Override
    @Transactional(readOnly = true)
    public String exportCsv() {
        List<SurveyQuestion> questions = orderedQuestions();
        List<Object> header = new ArrayList<>(List.of("Respondent", "Submitted", "Age", "Sex", "Education",
                "Household size", "Length of residency", "Monthly income", "Experienced disaster"));
        for (SurveyQuestion q : questions) {
            header.add(SHORT_LABEL.get(q.getSection()).charAt(0) + String.valueOf(q.getItemNo())
                    + (q.isNegative() ? " (neg)" : ""));
        }
        header.addAll(List.of("Knowledge mean", "Attitudes mean", "Practices mean", "Overall mean"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder("\uFEFF").append(Csv.row(header));
        int i = 1;
        for (SurveyResponse r : responseRepository.findAllWithAnswers()) {
            Map<Long, Integer> raw = new HashMap<>();
            r.getAnswers().forEach(ans -> raw.put(ans.getQuestion().getId(), ans.getRawValue()));
            List<Object> row = new ArrayList<>(List.of(
                    String.format("R-%03d", i++),
                    r.getSubmittedAt() == null ? "" : r.getSubmittedAt().format(fmt),
                    r.getAgeGroup().getLabel(), r.getSex().getLabel(), r.getEducation().getLabel(),
                    r.getHouseholdSize().getLabel(), r.getResidency().getLabel(), r.getIncome().getLabel(),
                    r.isExperiencedDisaster() ? "Yes" : "No"));
            for (SurveyQuestion q : questions) row.add(raw.get(q.getId()));
            row.add(Scale.round2(r.getKnowledgeMean()));
            row.add(Scale.round2(r.getAttitudeMean()));
            row.add(Scale.round2(r.getPracticeMean()));
            row.add(Scale.round2(r.getOverallMean()));
            sb.append(Csv.row(row));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- helpers

    private List<SurveyQuestion> orderedQuestions() {
        return questionRepository.findAll().stream()
                .sorted(Comparator.comparing(SurveyQuestion::getSection).thenComparingInt(SurveyQuestion::getItemNo))
                .toList();
    }

    private static int partNo(SurveySection s) {
        return switch (s) {
            case KNOWLEDGE -> 2;
            case ATTITUDE -> 3;
            case PRACTICE -> 4;
        };
    }

    private static double avg(double[] s) {
        return s == null || s[1] == 0 ? 0 : s[0] / s[1];
    }

    @SafeVarargs
    private static <E extends Enum<E> & Labeled> List<OptionDto> options(E... values) {
        return Arrays.stream(values).map(OptionDto::of).toList();
    }

    @SafeVarargs
    private static <E extends Enum<E> & Labeled> List<String> labels(E... values) {
        return Arrays.stream(values).map(Labeled::getLabel).toList();
    }

    private static double[] values(List<SurveyResponse> rs, ToDoubleFunction<SurveyResponse> f) {
        return rs.stream().mapToDouble(f).toArray();
    }

    private static <E extends Enum<E> & Labeled> ProfileTable profileTable(String variable, List<SurveyResponse> rs,
            Function<SurveyResponse, E> getter, E[] values) {
        Map<E, Integer> counts = new LinkedHashMap<>();
        for (E v : values) counts.put(v, 0);
        rs.forEach(r -> counts.merge(getter.apply(r), 1, Integer::sum));
        List<ProfileRow> rows = counts.entrySet().stream()
                .map(e -> new ProfileRow(e.getKey().getLabel(), e.getValue(), pct(e.getValue(), rs.size())))
                .toList();
        return new ProfileTable(variable, rows);
    }

    private static ProfileTable yesNoTable(String variable, List<SurveyResponse> rs) {
        int yes = (int) rs.stream().filter(SurveyResponse::isExperiencedDisaster).count();
        int no = rs.size() - yes;
        return new ProfileTable(variable, List.of(
                new ProfileRow("Yes", yes, pct(yes, rs.size())),
                new ProfileRow("No", no, pct(no, rs.size()))));
    }

    private static double pct(int part, int total) {
        return total == 0 ? 0 : Scale.round2(part * 100.0 / total);
    }

    private static DomainResult domain(SurveySection section, double[] means, Map<SurveySection, List<ItemResult>> items) {
        double mean = KapStatistics.mean(means);
        return DomainResult.builder()
                .key(section.name())
                .label(section.getLabel())
                .mean(Scale.round2(mean))
                .sd(Scale.round2(KapStatistics.sd(means)))
                .level(Scale.level(mean))
                .items(items.getOrDefault(section, List.of()))
                .build();
    }

    private static CorrelationResult correlation(String variables, double[] x, double[] y) {
        KapStatistics.TestResult t = KapStatistics.pearson(x, y);
        if (t == null) {
            return CorrelationResult.builder().variables(variables).decision("Not enough data")
                    .note(x.length < 3 ? "At least 3 respondents are needed."
                            : "Responses have no variation yet, so r cannot be computed.")
                    .build();
        }
        return CorrelationResult.builder()
                .variables(variables)
                .r(Scale.round4(t.statistic()))
                .p(Scale.round4(t.pValue()))
                .strength(KapStatistics.strength(t.statistic()))
                .decision(KapStatistics.decision(t.pValue()))
                .build();
    }

    private static DifferenceResult difference(String variable, List<SurveyResponse> rs,
            Function<SurveyResponse, String> key, List<String> orderedLabels) {
        Map<String, List<Double>> groups = new LinkedHashMap<>();
        orderedLabels.forEach(l -> groups.put(l, new ArrayList<>()));
        rs.forEach(r -> groups.get(key.apply(r)).add(r.getOverallMean()));

        List<GroupMean> groupMeans = new ArrayList<>();
        List<double[]> usable = new ArrayList<>();
        List<String> excluded = new ArrayList<>();
        groups.forEach((label, list) -> {
            if (list.isEmpty()) return;
            double[] arr = list.stream().mapToDouble(Double::doubleValue).toArray();
            groupMeans.add(new GroupMean(label, arr.length, Scale.round2(KapStatistics.mean(arr))));
            if (arr.length >= 2) usable.add(arr);
            else excluded.add(label);
        });

        boolean twoCategoryVariable = orderedLabels.size() == 2;
        String test = twoCategoryVariable ? "Independent t-test" : "One-way ANOVA";
        KapStatistics.TestResult result = twoCategoryVariable
                ? (usable.size() == 2 ? KapStatistics.tTest(usable.get(0), usable.get(1)) : null)
                : KapStatistics.anova(usable);

        String note = excluded.isEmpty() ? null
                : "Excluded (fewer than 2 respondents): " + String.join(", ", excluded) + ".";
        if (result == null) {
            String reason = usable.size() < 2 ? "Needs at least two groups with 2 or more respondents each."
                    : "Scores have no variation yet.";
            return DifferenceResult.builder().variable(variable).test(test).decision("Not enough data")
                    .note(note == null ? reason : note + " " + reason).groups(groupMeans).build();
        }
        return DifferenceResult.builder()
                .variable(variable)
                .test(test)
                .statistic(Scale.round4(result.statistic()))
                .p(Scale.round4(result.pValue()))
                .decision(KapStatistics.decision(result.pValue()))
                .note(note)
                .groups(groupMeans)
                .build();
    }
}