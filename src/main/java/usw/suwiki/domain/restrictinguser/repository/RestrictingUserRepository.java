package usw.suwiki.domain.restrictinguser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.restrictinguser.RestrictingUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestrictingUserRepository extends JpaRepository<RestrictingUser, Long> {

    List<RestrictingUser> findByRestrictingDateBefore(LocalDateTime localDateTime);

    Optional<RestrictingUser> findByUserIdx(Long userIdx);

    void deleteByUserIdx(Long userIdx);
}
