package ph.gov.tabunok.drrm.functions.directory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.directory.entity.EvacuationCenter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvacuationCenterDto {

    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "Location is required.")
    @Size(max = 200)
    private String location;

    @Size(max = 150)
    private String puroksServed;

    @Min(value = 0, message = "Capacity cannot be negative.")
    private Integer capacity;

    @Size(max = 100)
    private String contactPerson;

    @Size(max = 30)
    private String contactNumber;

    private boolean open;

    public static EvacuationCenterDto from(EvacuationCenter c) {
        return EvacuationCenterDto.builder()
                .id(c.getId())
                .name(c.getName())
                .location(c.getLocation())
                .puroksServed(c.getPuroksServed())
                .capacity(c.getCapacity())
                .contactPerson(c.getContactPerson())
                .contactNumber(c.getContactNumber())
                .open(c.isOpen())
                .build();
    }
}