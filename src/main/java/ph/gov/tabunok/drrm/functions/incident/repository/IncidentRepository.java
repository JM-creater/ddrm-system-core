package ph.gov.tabunok.drrm.functions.incident.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ph.gov.tabunok.drrm.functions.incident.entity.Incident;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentStatus;
import ph.gov.tabunok.drrm.functions.incident.entity.IncidentType;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Filtered listing. When {@code hideRejected} is true, rejected reports are only
     * returned to the resident who filed them.
     */
    @EntityGraph(attributePaths = { "reporter" })
    @Query("""
            select i from Incident i
            where (:type is null or i.type = :type)
              and (:status is null or i.status = :status)
              and (:purok is null or i.purok = :purok)
              and (:reporterId is null or i.reporter.id = :reporterId)
              and (:q is null
                   or lower(i.purok) like :q
                   or lower(i.description) like :q
                   or lower(coalesce(i.exactLocation, '')) like :q
                   or lower(concat(i.reporter.firstName, ' ', i.reporter.lastName)) like :q
                   or i.id = :reportId)
              and (:hideRejected = false or i.status <> :rejected or i.reporter.id = :viewerId)
            """)
    Page<Incident> search(@Param("type") IncidentType type,
                          @Param("status") IncidentStatus status,
                          @Param("purok") String purok,
                          @Param("reporterId") Long reporterId,
                          @Param("q") String q,
                          @Param("reportId") Long reportId,
                          @Param("hideRejected") boolean hideRejected,
                          @Param("rejected") IncidentStatus rejected,
                          @Param("viewerId") Long viewerId,
                          Pageable pageable);

    long countByStatus(IncidentStatus status);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    long countByReporterId(Long reporterId);

    @Query("select i.type, count(i) from Incident i where i.status <> :rejected group by i.type")
    List<Object[]> countByType(@Param("rejected") IncidentStatus rejected);

    @Query("select i.status, count(i) from Incident i group by i.status")
    List<Object[]> countGroupedByStatus();

    @Query("select i.purok, count(i) from Incident i where i.status <> :rejected group by i.purok order by count(i) desc")
    List<Object[]> countByPurok(@Param("rejected") IncidentStatus rejected);

    @EntityGraph(attributePaths = { "reporter" })
    List<Incident> findTop5ByReporterIdOrderByCreatedAtDesc(Long reporterId);

    @EntityGraph(attributePaths = { "reporter" })
    List<Incident> findTop6ByStatusOrderByCreatedAtAsc(IncidentStatus status);
}