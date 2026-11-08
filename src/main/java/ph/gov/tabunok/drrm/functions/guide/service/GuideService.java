package ph.gov.tabunok.drrm.functions.guide.service;

import java.util.List;

import ph.gov.tabunok.drrm.functions.guide.dto.GuideDto;

public interface GuideService {

    List<GuideDto> list();

    GuideDto create(GuideDto req);

    GuideDto update(Long id, GuideDto req);

    void delete(Long id);
}