package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Stage;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.repo.ReportRepository;
import cn.edu.xmu.yeahbuddy.model.ReportDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 团队报告任务
 */
@Service
public class ReportService {

    @NonNls
    private static Log log = LogFactory.getLog(ReportService.class);
    private ReportRepository reportRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param reportRepository Autowired
     */
    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * 查找团队项目报告
     *
     * @param id 团队项目报告主键
     * @return 团队项目报告
     */
    @Transactional
    public Optional<Report> findById(int id) {
        log.debug("Finding Report " + id);
        return reportRepository.findById(id);
    }

    /**
     * 查找团队项目报告
     *
     * @param team  团队
     * @param stage 阶段
     * @return 团队项目报告
     */
    @Transactional
    public Optional<Report> find(Team team, Stage stage) {
        log.debug("Finding Report " + team + ", " + stage);
        return reportRepository.find(team, stage);
    }

    /**
     * 查找某团队的所有项目报告
     *
     * @param teamId 团队ID
     * @return 所有团队项目报告
     */
    @Transactional
    public List<Report> findByTeamId(int teamId) {
        log.debug("Finding all reports of Team " + teamId);
        return reportRepository.findByTeam_Id(teamId);
    }

    /**
     * 查找所有项目报告
     *
     * @return 所有项目报告
     */
    @Transactional
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    /**
     * 新建团队项目报告
     *
     * @param team  团队I
     * @param stage 阶段
     * @param title 团队项目报告标题
     * @return 新注册的团队项目报告
     */
    @Transactional
    public Report createReport(Team team, Stage stage, String title) throws IdentifierAlreadyExistsException {
        log.debug("Trying to create Report with  " + team + ", " + stage);
        if (reportRepository.find(team, stage).isPresent()) {
            log.info("Fail to create Report with id " + team + ", " + stage + ": id already exist");
            throw new IdentifierAlreadyExistsException("report.id.exist", String.format("%s, %s", team, stage));
        }

        Report report = new Report(team, stage);
        report.setSubmitted(false);
        report.setTitle(title);
        reportRepository.save(report);
        log.debug("Created new Report with id " + team + ", " + stage);
        return report;
    }

    /**
     * 删除团队项目报告
     *
     * @param id 团队项目报告ID
     */
    @Transactional
    public void deleteTeamReport(int id) {
        log.debug("Delete Report with id" + id);
        reportRepository.deleteById(id);
    }

    /**
     * 修改团队项目报告
     *
     * @param id  团队项目报告ID
     * @param dto 团队项目报告的Dto
     * @return 团队项目报告
     */
    @Transactional
    public Report updateReport(int id, ReportDto dto) {
        log.debug("Trying to update Report " + id);
        Optional<Report> r = reportRepository.queryById(id);

        if (!r.isPresent()) {
            log.info("Failed to load Report " + id + ": not found");
            throw new IdentifierNotExistsException("report.id.not_found", id);
        }
        Report report = r.get();

        if (dto.getTitle() != null) {
            log.trace("Update title for Report " + id + ":" + report.getTitle() +
                              " -> " + dto.getTitle());
            report.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            log.trace("Update content for Report with id " + id + ":" + report.getContent().toString() +
                              " -> " + dto.getContent().toString());
            report.setContent(dto.getContent());
        }
        if (dto.getFiles() != null) {
            log.trace("Update files for Report with id " + id + ":" + report.getFiles().toString() +
                              " -> " + dto.getFiles().toString());
            report.setFiles(dto.getFiles());
        }
        if (dto.getSubmitted() != null) {
            log.trace("Update submitted for Report with id " + id + ":" + report.isSubmitted() +
                              " -> " + dto.getSubmitted());
            report.setSubmitted(dto.getSubmitted());
        }

        return reportRepository.save(report);
    }
}
