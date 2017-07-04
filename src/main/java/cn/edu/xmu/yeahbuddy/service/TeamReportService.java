package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.ReviewKey;
import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.domain.TeamStage;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamReportRepository;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

/**
 * 团队报告任务
 */
@Service
public class TeamReportService {

    @NonNls
    private static Log log= LogFactory.getLog(TeamReportService.class);

    private TeamReportRepository teamReportRepository;

    private TeamService teamService;

    /**
     * @param teamReportRepository Autowired
     * @param teamService Autowired
     */
    @Autowired
    public TeamReportService(TeamReportRepository teamReportRepository,TeamService teamService){
        this.teamReportRepository=teamReportRepository;
        this.teamService=teamService;
    }


}
