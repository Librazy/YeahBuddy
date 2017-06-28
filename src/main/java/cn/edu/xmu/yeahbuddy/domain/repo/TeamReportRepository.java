package cn.edu.xmu.yeahbuddy.domain.repo;

import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.domain.TeamStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamReportRepository extends JpaRepository<TeamReport, TeamStage> {
    List<TeamReport> findByTeamStageTeamId(int teamId);

    TeamReport findBySubmitted(Boolean submitted);
}
