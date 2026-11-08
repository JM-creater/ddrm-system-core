package ph.gov.tabunok.drrm.functions.alert.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.alert.dto.AlertDto;
import ph.gov.tabunok.drrm.functions.alert.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /** Bulletin: active warnings first, then lifted ones. */
    @GetMapping
    public List<AlertDto> all() {
        return alertService.all();
    }

    @GetMapping("/active")
    public List<AlertDto> active() {
        return alertService.active();
    }
}