package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.model.ReportDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
public class ReportController {

    @NonNls
    private static Log log = LogFactory.getLog(ReportController.class);

    private final MessageSource messageSource;

    private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService, MessageSource messageSource) {
        this.reportService = reportService;
        this.messageSource = messageSource;
    }


    //TODO: 报告编辑页面
    @GetMapping("/report/{reportId:\\d+}")
    @PreAuthorize("hasRole('TEAM') && @reportService.findById(#reportId).get().team.id == T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id")
    public String report(@PathVariable int reportId, Model model) {
        Optional<Report> report = reportService.findById(reportId);
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("report.id.not_found", reportId);
        }
        model.addAttribute("formAction", String.format("/report/%d", reportId));
        model.addAttribute("report", report.get());
        model.addAttribute("teamId", report.get().getTeamId());
        if (report.get().isSubmitted()) {
            model.addAttribute("readOnly", true);
        }
        return "team/report";
    }

    @PutMapping("/report/{reportId:\\d+}")
    @PreAuthorize("hasRole('TEAM') && @reportService.findById(#reportId).get().team.id == T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id")
    public ResponseEntity<Map<String, String>> updateReport(@PathVariable int reportId, ReportDto reportDto) {
        log.debug("Update report ");

        Optional<Report> report = reportService.findById(reportId);
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("report.id.not_found", reportId);
        }

        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();

        if (report.get().isSubmitted()) {
            result.put("status", "409");
            result.put("error", messageSource.getMessage("http.status.409", new Object[]{}, locale));
            result.put("message", messageSource.getMessage("report.already.submitted", new Object[]{}, locale));
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }

        reportService.updateReport(reportId, reportDto);

        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("report.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }
}
