package ph.gov.tabunok.drrm.functions.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ph.gov.tabunok.drrm.functions.user.entity.Role;
import ph.gov.tabunok.drrm.functions.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRole(Role role);

    @Query("""
            select u from User u
            where u.role = :role
              and (:q is null
                   or lower(u.firstName) like :q
                   or lower(u.lastName) like :q
                   or lower(u.email) like :q
                   or lower(coalesce(u.purok, '')) like :q)
            """)
    Page<User> search(@Param("role") Role role, @Param("q") String q, Pageable pageable);
}