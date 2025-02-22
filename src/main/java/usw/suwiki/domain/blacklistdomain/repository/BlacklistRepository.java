package usw.suwiki.domain.blacklistdomain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.blacklistdomain.BlacklistDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistDomain, Long> {

    Optional<BlacklistDomain> findByUserIdx(Long userIdx);

    List<BlacklistDomain> findByExpiredAtBefore(LocalDateTime targetTime);

    void deleteByUserIdx(Long userIdx);
}