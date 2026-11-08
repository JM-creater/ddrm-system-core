package ph.gov.tabunok.drrm.functions.incident.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentStatusRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.incident.entity.Incident;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

public interface IncidentService {

    int MAX_PHOTOS = 3;

    record Filter(IncidentType type, IncidentStatus status, String purok, String q, boolean mine) {
    }

    IncidentDto create(AppUserPrincipal me, IncidentRequest req, List<MultipartFile> photos, MultipartFile video);

    PageResponse<IncidentDto> list(AppUserPrincipal me, Filter filter, int page, int size);

    IncidentDto get(AppUserPrincipal me, Long id);

    IncidentDto updateStatus(AppUserPrincipal admin, Long id, IncidentStatusRequest req);

    void delete(AppUserPrincipal me, Long id);

    String exportCsv(AppUserPrincipal me, Filter filter);

    IncidentDto toDto(Incident i, AppUserPrincipal me);

    static String reportNo(Incident i) {
        int year = i.getCreatedAt() == null ? java.time.Year.now().getValue() : i.getCreatedAt().getYear();
        return "TBN-" + year + "-" + String.format("%04d", i.getId());
    }
}