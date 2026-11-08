package ph.gov.tabunok.drrm.functions.guide.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.guide.dto.GuideDto;
import ph.gov.tabunok.drrm.functions.guide.entity.Guide;
import ph.gov.tabunok.drrm.functions.guide.repository.GuideRepository;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GuideDto> list() {
        return guideRepository.findAllByOrderBySortOrderAscIdAsc().stream().map(GuideDto::from).toList();
    }

    @Override
    @Transactional
    public GuideDto create(GuideDto req) {
        Guide guide = new Guide();
        apply(guide, req);
        return GuideDto.from(guideRepository.saveAndFlush(guide));
    }

    @Override
    @Transactional
    public GuideDto update(Long id, GuideDto req) {
        Guide guide = guideRepository.findById(id).orElseThrow(() -> ApiException.notFound("Guide"));
        apply(guide, req);
        return GuideDto.from(guideRepository.saveAndFlush(guide));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        guideRepository.delete(guideRepository.findById(id).orElseThrow(() -> ApiException.notFound("Guide")));
    }

    private void apply(Guide g, GuideDto req) {
        g.setHazard(req.getHazard());
        g.setTitle(req.getTitle().trim());
        g.setSummary(AuthService.blankToNull(req.getSummary()));
        g.setBeforeSteps(cleanSteps(req.getBeforeSteps()));
        g.setDuringSteps(cleanSteps(req.getDuringSteps()));
        g.setAfterSteps(cleanSteps(req.getAfterSteps()));
        g.setSortOrder(req.getSortOrder());
    }

    /** Normalises line endings and drops blank lines. */
    private static String cleanSteps(String steps) {
        if (steps == null) return null;
        String cleaned = steps.replace("\r\n", "\n").lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
        return cleaned.isEmpty() ? null : cleaned;
    }
}