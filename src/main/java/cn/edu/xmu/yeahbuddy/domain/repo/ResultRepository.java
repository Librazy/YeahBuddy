package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Result;
import cn.edu.xmu.yeahbuddy.domain.Team;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer> {

    @NotNull
    Optional<Result> findByReport(Report report);

    List<Result> findByReport_Team(Team team);

    List<Result> findBySubmittedFalse();

    default List<Result> findByTeam(Team team) {
        return findByReport_Team(team);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Result> queryById(int id);
}
