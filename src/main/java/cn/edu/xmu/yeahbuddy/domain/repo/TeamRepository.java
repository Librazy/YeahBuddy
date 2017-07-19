package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Team;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @NotNull
    Optional<Team> findByUsername(String username);

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Team> queryById(int id);
}
