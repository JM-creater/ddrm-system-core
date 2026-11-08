package ph.gov.tabunok.drrm.functions.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.directory.entity.Hotline;

public interface HotlineRepository extends JpaRepository<Hotline, Long> {

    List<Hotline> findAllByOrderBySortOrderAscIdAsc();
}