package ph.gov.tabunok.drrm.functions.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountItem {

    private String key;
    private String label;
    private long count;
}