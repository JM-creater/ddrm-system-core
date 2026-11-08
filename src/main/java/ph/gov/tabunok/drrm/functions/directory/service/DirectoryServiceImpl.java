package ph.gov.tabunok.drrm.functions.directory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.directory.dto.EvacuationCenterDto;
import ph.gov.tabunok.drrm.functions.directory.dto.HotlineDto;
import ph.gov.tabunok.drrm.functions.directory.entity.EvacuationCenter;
import ph.gov.tabunok.drrm.functions.directory.entity.Hotline;
import ph.gov.tabunok.drrm.functions.directory.repository.EvacuationCenterRepository;
import ph.gov.tabunok.drrm.functions.directory.repository.HotlineRepository;

@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final EvacuationCenterRepository centerRepository;
    private final HotlineRepository hotlineRepository;

    // ---- Evacuation centers ----

    @Override
    @Transactional(readOnly = true)
    public List<EvacuationCenterDto> centers() {
        return centerRepository.findAllByOrderByNameAsc().stream().map(EvacuationCenterDto::from).toList();
    }

    @Override
    @Transactional
    public EvacuationCenterDto saveCenter(Long id, EvacuationCenterDto req) {
        EvacuationCenter c = id == null ? new EvacuationCenter()
                : centerRepository.findById(id).orElseThrow(() -> ApiException.notFound("Evacuation center"));
        c.setName(req.getName().trim());
        c.setLocation(req.getLocation().trim());
        c.setPuroksServed(AuthService.blankToNull(req.getPuroksServed()));
        c.setCapacity(req.getCapacity());
        c.setContactPerson(AuthService.blankToNull(req.getContactPerson()));
        c.setContactNumber(AuthService.blankToNull(req.getContactNumber()));
        c.setOpen(req.isOpen());
        return EvacuationCenterDto.from(centerRepository.save(c));
    }

    @Override
    @Transactional
    public void deleteCenter(Long id) {
        centerRepository.delete(centerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Evacuation center")));
    }

    // ---- Hotlines ----

    @Override
    @Transactional(readOnly = true)
    public List<HotlineDto> hotlines() {
        return hotlineRepository.findAllByOrderBySortOrderAscIdAsc().stream().map(HotlineDto::from).toList();
    }

    @Override
    @Transactional
    public HotlineDto saveHotline(Long id, HotlineDto req) {
        Hotline h = id == null ? new Hotline()
                : hotlineRepository.findById(id).orElseThrow(() -> ApiException.notFound("Hotline"));
        h.setAgency(req.getAgency().trim());
        h.setNumber(req.getNumber().trim());
        h.setDescription(AuthService.blankToNull(req.getDescription()));
        h.setSortOrder(req.getSortOrder());
        return HotlineDto.from(hotlineRepository.save(h));
    }

    @Override
    @Transactional
    public void deleteHotline(Long id) {
        hotlineRepository.delete(hotlineRepository.findById(id).orElseThrow(() -> ApiException.notFound("Hotline")));
    }
}