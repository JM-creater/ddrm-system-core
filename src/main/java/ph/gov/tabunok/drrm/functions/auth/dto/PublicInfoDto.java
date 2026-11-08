package ph.gov.tabunok.drrm.functions.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.directory.dto.HotlineDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicInfoDto {

    private List<AlertDto> activeAlerts;
    private List<HotlineDto> hotlines;
    private List<String> puroks;
}