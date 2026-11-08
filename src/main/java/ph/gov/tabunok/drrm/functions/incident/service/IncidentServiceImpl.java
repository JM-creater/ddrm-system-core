package ph.gov.tabunok.drrm.functions.incident.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.exception.ApiException;
import ph.gov.tabunok.drrm.functions.auth.service.AuthService;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentStatusRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.MediaDto;
import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.incident.entity.Incident;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentMedia;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.MediaKind;
import ph.gov.tabunok.drrm.functions.incident.repository.IncidentRepository;
import ph.gov.tabunok.drrm.functions.user.entity.User;
import ph.gov.tabunok.drrm.functions.user.repository.UserRepository;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final FileStorageService storage;
    private final AuthService authService;

    @Override
    @Transactional
    public IncidentDto create(AppUserPrincipal me, IncidentRequest req, List<MultipartFile> photos, MultipartFile video) {
        authService.requireValidPurok(req.getPurok());
        List<MultipartFile> photoFiles = photos == null ? List.of()
                : photos.stream().filter(f -> f != null && !f.isEmpty()).toList();
        if (photoFiles.size() > MAX_PHOTOS) {
            throw ApiException.badRequest("You can attach up to " + MAX_PHOTOS + " photos only.");
        }
        User reporter = userRepository.findById(me.getId()).orElseThrow(() -> ApiException.notFound("User"));

        Incident incident = Incident.builder()
                .type(req.getType())
                .severity(req.getSeverity())
                .purok(req.getPurok())
                .description(req.getDescription().trim())
                .exactLocation(AuthService.blankToNull(req.getExactLocation()))
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .status(IncidentStatus.PENDING)
                .reporter(reporter)
                .build();

        List<String> saved = new ArrayList<>();
        // Remove written files again if anything fails before the transaction commits.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) saved.forEach(storage::deleteQuietly);
            }
        });
        for (MultipartFile f : photoFiles) {
            incident.addMedia(toMedia(storage.store(f, MediaKind.PHOTO), MediaKind.PHOTO));
            saved.add(incident.getMedia().get(incident.getMedia().size() - 1).getStoredPath());
        }
        if (video != null && !video.isEmpty()) {
            incident.addMedia(toMedia(storage.store(video, MediaKind.VIDEO), MediaKind.VIDEO));
            saved.add(incident.getMedia().get(incident.getMedia().size() - 1).getStoredPath());
        }
        return toDto(incidentRepository.save(incident), me);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<IncidentDto> list(AppUserPrincipal me, Filter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 60),
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
        return PageResponse.of(search(me, filter, pageable), i -> toDto(i, me));
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentDto get(AppUserPrincipal me, Long id) {
        return toDto(findVisible(me, id), me);
    }

    @Override
    @Transactional
    public IncidentDto updateStatus(AppUserPrincipal admin, Long id, IncidentStatusRequest req) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> ApiException.notFound("Report"));
        incident.setStatus(req.getStatus());
        incident.setRemarks(AuthService.blankToNull(req.getRemarks()));
        incident.setReviewedBy(userRepository.getReferenceById(admin.getId()));
        return toDto(incidentRepository.saveAndFlush(incident), admin);
    }

    @Override
    @Transactional
    public void delete(AppUserPrincipal me, Long id) {
        Incident incident = findVisible(me, id);
        if (!canDelete(incident, me)) {
            throw ApiException.forbidden("Only pending reports you filed can be withdrawn.");
        }
        List<String> files = incident.getMedia().stream().map(IncidentMedia::getStoredPath).toList();
        incidentRepository.delete(incident);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                files.forEach(storage::deleteQuietly);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv(AppUserPrincipal me, Filter filter) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append(Csv.row(List.of("Report No.", "Date filed", "Type", "Severity", "Status", "Purok",
                "Exact location", "Latitude", "Longitude", "Description", "Reported by", "Remarks", "Attachments")));
        Pageable all = PageRequest.of(0, 5000, Sort.by(Sort.Direction.DESC, "createdAt"));
        for (Incident i : search(me, filter, all)) {
            List<Object> cells = new ArrayList<>();
            cells.add(IncidentService.reportNo(i));
            cells.add(i.getCreatedAt() == null ? "" : i.getCreatedAt().format(fmt));
            cells.add(i.getType().getLabel());
            cells.add(i.getSeverity().getLabel());
            cells.add(i.getStatus().getLabel());
            cells.add(i.getPurok());
            cells.add(i.getExactLocation());
            cells.add(i.getLatitude());
            cells.add(i.getLongitude());
            cells.add(i.getDescription());
            cells.add(i.getReporter().getFullName());
            cells.add(i.getRemarks());
            cells.add(i.getMedia().size());
            sb.append(Csv.row(cells));
        }
        return sb.toString();
    }

    private static final java.util.regex.Pattern REPORT_NO =
            java.util.regex.Pattern.compile("^(?:TBN-\\d{4}-)?0*(\\d{1,9})$", java.util.regex.Pattern.CASE_INSENSITIVE);

    private Page<Incident> search(AppUserPrincipal me, Filter f, Pageable pageable) {
        String like = f.q() == null || f.q().isBlank() ? null : "%" + f.q().trim().toLowerCase() + "%";
        Long reportId = null;
        if (like != null) {
            var m = REPORT_NO.matcher(f.q().trim());
            if (m.matches()) reportId = Long.valueOf(m.group(1));
        }
        String purok = f.purok() == null || f.purok().isBlank() ? null : f.purok();
        return incidentRepository.search(f.type(), f.status(), purok, f.mine() ? me.getId() : null, like, reportId,
                !me.isAdmin(), IncidentStatus.REJECTED, me.getId(), pageable);
    }

    private Incident findVisible(AppUserPrincipal me, Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() -> ApiException.notFound("Report"));
        boolean own = incident.getReporter().getId().equals(me.getId());
        if (!me.isAdmin() && incident.getStatus() == IncidentStatus.REJECTED && !own) {
            throw ApiException.notFound("Report");
        }
        return incident;
    }

    private boolean canDelete(Incident i, AppUserPrincipal me) {
        return me.isAdmin()
                || (i.getReporter().getId().equals(me.getId()) && i.getStatus() == IncidentStatus.PENDING);
    }

    private static IncidentMedia toMedia(FileStorageService.StoredFile f, MediaKind kind) {
        return IncidentMedia.builder()
                .kind(kind)
                .storedPath(f.path())
                .contentType(f.contentType())
                .sizeBytes(f.size())
                .build();
    }

    @Override
    public IncidentDto toDto(Incident i, AppUserPrincipal me) {
        User reporter = i.getReporter();
        return IncidentDto.builder()
                .id(i.getId())
                .reportNo(IncidentService.reportNo(i))
                .type(i.getType())
                .typeLabel(i.getType().getLabel())
                .severity(i.getSeverity())
                .severityLabel(i.getSeverity().getLabel())
                .purok(i.getPurok())
                .description(i.getDescription())
                .exactLocation(i.getExactLocation())
                .latitude(i.getLatitude())
                .longitude(i.getLongitude())
                .status(i.getStatus())
                .statusLabel(i.getStatus().getLabel())
                .remarks(i.getRemarks())
                .reporterId(reporter.getId())
                .reporterName(reporter.getFullName())
                // Contact numbers are only shown to barangay officers
                .reporterContact(me != null && me.isAdmin() ? reporter.getContactNumber() : null)
                .reviewedByName(i.getReviewedBy() == null ? null : i.getReviewedBy().getFullName())
                .media(i.getMedia().stream()
                        .map(m -> new MediaDto(m.getId(), m.getKind(), storage.resolveUrl(m.getStoredPath()), m.getContentType()))
                        .toList())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .canDelete(me != null && canDelete(i, me))
                .build();
    }
}