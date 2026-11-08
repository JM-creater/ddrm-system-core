package ph.gov.tabunok.drrm.functions.directory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.directory.entity.Hotline;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotlineDto {

    private Long id;

    @NotBlank(message = "Agency is required.")
    @Size(max = 120)
    private String agency;

    @NotBlank(message = "Number is required.")
    @Size(max = 40)
    private String number;

    @Size(max = 200)
    private String description;

    private int sortOrder;

    public static HotlineDto from(Hotline h) {
        return HotlineDto.builder()
                .id(h.getId())
                .agency(h.getAgency())
                .number(h.getNumber())
                .description(h.getDescription())
                .sortOrder(h.getSortOrder())
                .build();
    }
}