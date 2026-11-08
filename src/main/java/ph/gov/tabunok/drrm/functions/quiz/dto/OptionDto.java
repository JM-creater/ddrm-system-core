package ph.gov.tabunok.drrm.functions.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.Labeled;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    private String value;
    private String label;

    public static <E extends Enum<E> & Labeled> OptionDto of(E e) {
        return new OptionDto(e.name(), e.getLabel());
    }
}