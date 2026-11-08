package ph.gov.tabunok.drrm.functions.alert.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertRequest;
import ph.gov.tabunok.drrm.functions.alert.entity.Alert;
import ph.gov.tabunok.drrm.functions.alert.repository.AlertRepository;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlertDto> active() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc().stream().map(AlertDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertDto> all() {
        return alertRepository.findAllByOrderByActiveDescCreatedAtDesc().stream().map(AlertDto::from).toList();
    }

    @Override
    @Transactional
    public AlertDto create(Long adminId, AlertRequest req) {
        Alert alert = Alert.builder().active(true).postedBy(userRepository.getReferenceById(adminId)).build();
        apply(alert, req);
        return AlertDto.from(alertRepository.saveAndFlush(alert));
    }

    @Override
    @Transactional
    public AlertDto update(Long id, AlertRequest req) {
        Alert alert = find(id);
        apply(alert, req);
        return AlertDto.from(alert);
    }

    @Override
    @Transactional
    public AlertDto setActive(Long id, boolean active) {
        Alert alert = find(id);
        alert.setActive(active);
        alert.setLiftedAt(active ? null : LocalDateTime.now());
        return AlertDto.from(alert);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        alertRepository.delete(find(id));
    }

    private void apply(Alert alert, AlertRequest req) {
        alert.setTitle(req.getTitle().trim());
        alert.setMessage(req.getMessage().trim());
        alert.setLevel(req.getLevel());
        alert.setHazard(req.getHazard());
        alert.setAffectedArea(AuthService.blankToNull(req.getAffectedArea()));
    }

    private Alert find(Long id) {
        return alertRepository.findById(id).orElseThrow(() -> ApiException.notFound("Alert"));
    }
}