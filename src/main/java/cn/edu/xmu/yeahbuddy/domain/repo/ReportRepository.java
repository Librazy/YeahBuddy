package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.Report;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByTeamId(int teamId);

    @NotNull
    Optional<Report> findByTeamIdAndStageId(int teamId, int stageId);

    @NotNull
    default Optional<Report> find(int teamId, int stageId) {
        return findByTeamIdAndStageId(teamId, stageId);
    }
}
