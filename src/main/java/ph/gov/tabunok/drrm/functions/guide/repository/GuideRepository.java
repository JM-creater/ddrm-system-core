package ph.gov.tabunok.drrm.functions.guide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ph.gov.tabunok.drrm.functions.guide.entity.Guide;

public interface GuideRepository extends JpaRepository<Guide, Long> {

    List<Guide> findAllByOrderBySortOrderAscIdAsc();
}