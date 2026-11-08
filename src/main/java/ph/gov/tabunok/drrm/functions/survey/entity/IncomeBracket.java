package ph.gov.tabunok.drrm.functions.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Getter
@RequiredArgsConstructor
public enum IncomeBracket implements Labeled {
    BELOW_10K("Below ₱10,000"),
    FROM_10K_TO_20K("₱10,000–₱20,000"),
    FROM_20K_TO_40K("₱20,001–₱40,000"),
    ABOVE_40K("Above ₱40,000");

    private final String label;
}
