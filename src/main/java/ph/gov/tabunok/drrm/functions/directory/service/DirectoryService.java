package ph.gov.tabunok.drrm.functions.directory.service;

import java.util.List;

import ph.gov.tabunok.drrm.functions.directory.dto.EvacuationCenterDto;
import ph.gov.tabunok.drrm.functions.directory.dto.HotlineDto;

public interface DirectoryService {

    List<EvacuationCenterDto> centers();

    EvacuationCenterDto saveCenter(Long id, EvacuationCenterDto req);

    void deleteCenter(Long id);

    List<HotlineDto> hotlines();

    HotlineDto saveHotline(Long id, HotlineDto req);

    void deleteHotline(Long id);
}