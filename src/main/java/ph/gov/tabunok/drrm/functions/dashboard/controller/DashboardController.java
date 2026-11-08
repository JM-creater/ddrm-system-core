package ph.gov.tabunok.drrm.functions.dashboard.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.dashboard.dto.AdminDashboardDto;
import ph.gov.tabunok.drrm.functions.dashboard.dto.ResidentDashboardDto;
import ph.gov.tabunok.drrm.functions.dashboard.service.DashboardService;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public ResidentDashboardDto resident(@AuthenticationPrincipal AppUserPrincipal me) {
        return dashboardService.resident(me);
    }

    @GetMapping("/api/admin/dashboard")
    public AdminDashboardDto admin(@AuthenticationPrincipal AppUserPrincipal me) {
        return dashboardService.admin(me);
    }
}