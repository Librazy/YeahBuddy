package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public class ReportController {

    @NonNls
    private static Log log = LogFactory.getLog(ReportController.class);

    private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService){
        this.reportService=reportService;
    }

    @GetMapping("/team/{teamId:\\d+}/reports/{stageId:\\d+}")
    public String showSelectedReport(@PathVariable int teamId, @PathVariable int stageId, Model model) {
        Optional<Report> report = reportService.find(teamId, stageId);
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("report.team_stage.not_found", String.format("%d, %d", teamId, stageId));
        }

        model.addAttribute("report", report.get());
        model.addAttribute("formAction", String.format("/team/%d/reports/%d", teamId, stageId));
        return "team/reportInformation";
    }


}
