package ph.gov.tabunok.drrm.functions.dashboard.service;

import ph.gov.tabunok.drrm.functions.dashboard.dto.AdminDashboardDto;
import ph.gov.tabunok.drrm.functions.dashboard.dto.ResidentDashboardDto;
import ph.gov.tabunok.drrm.security.AppUserPrincipal;

public interface DashboardService {

    ResidentDashboardDto resident(AppUserPrincipal me);

    AdminDashboardDto admin(AppUserPrincipal me);
}