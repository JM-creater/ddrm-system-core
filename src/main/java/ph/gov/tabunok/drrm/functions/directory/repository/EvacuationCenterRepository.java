package ph.gov.tabunok.drrm.functions.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.directory.entity.EvacuationCenter;

public interface EvacuationCenterRepository extends JpaRepository<EvacuationCenter, Long> {

    List<EvacuationCenter> findAllByOrderByNameAsc();

    long countByOpenTrue();
}