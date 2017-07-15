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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/report/{reportId:\\d+}")
    //TODO
    public ResponseEntity<Map<String, Object>> showSelectedReport(@PathVariable int reportId, @ModelAttribute("report") Report report) {

        if (report.getId() == Integer.MIN_VALUE) {
            Optional<Report> r = reportService.findById(reportId);
            if (!r.isPresent()) {
                throw new ResourceNotFoundException("report.id.not_found", reportId);
            }
            report = r.get();
        }
        Map<String, Object> m = new HashMap<>();
        m.put("formAction", String.format("/reports/%d", reportId));
        m.put("report", report);

        return ResponseEntity.ok(m);
    }

    @PutMapping("/report/{reportId:\\d+}")
    public ResponseEntity<Map<String, String>> updateReport(@PathVariable int reportId, ReportDto reportDto) {
        log.debug("Update report ");

        Optional<Report> report = reportService.findById(reportId);
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("report.id.not_found", reportId);
        }

        reportService.updateReport(reportId, reportDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("report.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }
}
