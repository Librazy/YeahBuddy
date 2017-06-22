package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamReportRepository extends JpaRepository<TeamReport, TeamStage> {
    List<TeamReport> findByTeamStageTeamId(int teamId);

    TeamReport findBySubmitted(Boolean submitted);
}
