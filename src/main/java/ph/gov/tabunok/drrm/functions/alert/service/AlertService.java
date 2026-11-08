package ph.gov.tabunok.drrm.functions.alert.service;

import java.util.List;

import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertRequest;

public interface AlertService {

    List<AlertDto> active();

    List<AlertDto> all();

    AlertDto create(Long adminId, AlertRequest req);

    AlertDto update(Long id, AlertRequest req);

    AlertDto setActive(Long id, boolean active);

    void delete(Long id);
}