package ph.gov.tabunok.drrm.functions.incident.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentDto;
import ph.gov.tabunok.drrm.functions.incident.dto.IncidentRequest;
import ph.gov.tabunok.drrm.functions.incident.dto.PageResponse;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;
import ph.gov.tabunok.drrm.functions.incident.service.IncidentService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public PageResponse<IncidentDto> list(@AuthenticationPrincipal AppUserPrincipal me,
                                          @RequestParam(required = false) IncidentType type,
                                          @RequestParam(required = false) IncidentStatus status,
                                          @RequestParam(required = false) String purok,
                                          @RequestParam(required = false) String q,
                                          @RequestParam(defaultValue = "false") boolean mine,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "12") int size) {
        return incidentService.list(me, new IncidentService.Filter(type, status, purok, q, mine), page, size);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentDto create(@AuthenticationPrincipal AppUserPrincipal me,
                              @Valid @ModelAttribute IncidentRequest req,
                              @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                              @RequestPart(value = "video", required = false) MultipartFile video) {
        return incidentService.create(me, req, photos, video);
    }

    @GetMapping("/{id}")
    public IncidentDto get(@AuthenticationPrincipal AppUserPrincipal me, @PathVariable Long id) {
        return incidentService.get(me, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AppUserPrincipal me, @PathVariable Long id) {
        incidentService.delete(me, id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@AuthenticationPrincipal AppUserPrincipal me,
                                         @RequestParam(required = false) IncidentType type,
                                         @RequestParam(required = false) IncidentStatus status,
                                         @RequestParam(required = false) String purok,
                                         @RequestParam(required = false) String q,
                                         @RequestParam(defaultValue = "false") boolean mine) {
        String csv = incidentService.exportCsv(me, new IncidentService.Filter(type, status, purok, q, mine));
        return CsvResponses.attachment(csv, "tabunok-incident-reports-" + LocalDate.now() + ".csv");
    }
}