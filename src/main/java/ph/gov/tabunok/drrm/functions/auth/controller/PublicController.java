package ph.gov.tabunok.drrm.functions.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.config.AppProperties;
import ph.gov.tabunok.drrm.functions.alert.service.AlertService;
import ph.gov.tabunok.drrm.functions.auth.dto.PublicInfoDto;
import ph.gov.tabunok.drrm.functions.directory.service.DirectoryService;

/** Information that may be shown before signing in (login page, registration form). */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final AlertService alertService;
    private final DirectoryService directoryService;
    private final AppProperties properties;

    @GetMapping("/info")
    public PublicInfoDto info() {
        return PublicInfoDto.builder()
                .activeAlerts(alertService.active())
                .hotlines(directoryService.hotlines())
                .puroks(properties.getPuroks())
                .build();
    }
}