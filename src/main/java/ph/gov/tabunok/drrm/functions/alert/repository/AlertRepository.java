package ph.gov.tabunok.drrm.functions.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.alert.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByActiveTrueOrderByCreatedAtDesc();

    List<Alert> findAllByOrderByActiveDescCreatedAtDesc();

    long countByActiveTrue();
}