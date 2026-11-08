package ph.gov.tabunok.drrm.functions.incident.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.entity.MediaKind;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {

    private Long id;
    private MediaKind kind;
    private String url;
    private String contentType;
}