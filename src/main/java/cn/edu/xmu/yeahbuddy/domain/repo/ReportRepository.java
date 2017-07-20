package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Team;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByTeam_Id(int teamId);

    List<Report> findByStage(Stage stage);

    @NotNull
    Optional<Report> findByTeamAndStage(Team team, Stage stage);

    @NotNull
    default Optional<Report> find(Team team, Stage stage) {
        return findByTeamAndStage(team, stage);
    }

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Report> queryById(int id);
}
