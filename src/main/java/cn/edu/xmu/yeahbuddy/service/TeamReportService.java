package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.domain.TeamStage;
import cn.edu.xmu.yeahbuddy.domain.repo.TeamReportRepository;
import cn.edu.xmu.yeahbuddy.model.TeamReportDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 团队报告任务
 */
@Service
public class TeamReportService {

    @NonNls
    private static Log log = LogFactory.getLog(TeamReportService.class);

    private TeamReportRepository teamReportRepository;

    /**
     * @param teamReportRepository Autowired
     */
    @Autowired
    public TeamReportService(TeamReportRepository teamReportRepository) {
        this.teamReportRepository = teamReportRepository;
    }

    /**
     * 查找团队项目报告
     *
     * @param teamStage 团队项目报告主键
     * @return 团队项目报告
     */
    @Transactional
    public Optional<TeamReport> findById(TeamStage teamStage) {
        log.debug("Finding TeamReport " + teamStage);
        return teamReportRepository.findById(teamStage);
    }

    /**
     * 新建团队项目报告
     *
     * @param teamStage 团队项目报告主键
     * @param title     团队项目报告标题
     * @return 新注册的团队项目报告
     */
    @Transactional
    public TeamReport createTeamReport(TeamStage teamStage, String title) throws IdentifierAlreadyExistsException {
        log.debug("Trying to create TeamReport with id " + teamStage);
        if (teamReportRepository.findById(teamStage).isPresent()) {
            log.info("Fail to create TeamReport with id " + teamStage + ": id already exist");
            throw new IdentifierAlreadyExistsException("teamreport.id.exist");
        }

        TeamReport teamReport = new TeamReport(teamStage);
        teamReport.setSubmitted(false);
        teamReport.setTitle(title);
        teamReportRepository.save(teamReport);
        log.debug("Created new TeamReport with id " + teamReport.getTeamStage());
        return teamReport;
    }

    /**
     * 删除团队项目报告
     *
     * @param id 团队项目报告主键
     */
    @Transactional
    public void deleteTeamReport(TeamStage id) {
        log.debug("Delete TeamReport with id" + id);
        teamReportRepository.deleteById(id);
    }

    /**
     * 修改团队项目报告
     *
     * @param id
     * @param dto 团队项目报告的dto
     * @return 团队项目报告
     */
    @Transactional
    public TeamReport updateTeamReport(TeamStage id,TeamReportDto dto){
        log.debug("Trying to update TeamReport "+id);
        TeamReport teamReport=teamReportRepository.getOne(dto.getTeamStage());

        if(dto.getTitle()!=null){
            log.trace("Update title for TeamReport "+id+":"+teamReport.getTitle()+
                        " -> "+dto.getTitle());
            teamReport.setTitle(dto.getTitle());
        }
        if(dto.getContent()!=null){
            log.trace("Update content for TeamReport "+id+":"+teamReport.getContent().toString()+
                    " -> "+dto.getContent().toString());
            teamReport.setContent(dto.getContent());
        }
        if(dto.getFiles()!=null){
            log.trace("Update files for TeamReport "+id+":"+teamReport.getFiles().toString()+
                    " -> "+dto.getFiles().toString());
            teamReport.setFiles(dto.getFiles());
        }
        if(dto.getSubmitted()!=null) {
            teamReport.setSubmitted(dto.getSubmitted());
        }

        return teamReportRepository.save(teamReport);
    }
}
