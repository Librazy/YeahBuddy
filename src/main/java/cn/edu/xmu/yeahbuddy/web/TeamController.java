package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.ReviewService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class TeamController {

    @NonNls
    private static Log log = LogFactory.getLog(TeamController.class);

    private final TeamService teamService;

    private final ReportService reportService;

    private final MessageSource messageSource;

    private final ReviewService reviewService;

    @Autowired
    public TeamController(TeamService teamService, ReportService reportService, MessageSource messageSource, ReviewService reviewService) {
        this.teamService = teamService;
        this.reportService = reportService;
        this.messageSource = messageSource;
        this.reviewService = reviewService;
    }

    @GetMapping("/team/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "team/login";
    }

    @GetMapping({"/team", "/team/"})
    @PreAuthorize("hasRole('TEAM')")
    public String index(Model model) {
        String email = ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        model.addAttribute("name", email);
        return "team/index";
    }

    @PostMapping("/team")
    @PreAuthorize("hasAuthority('ManageTeam')")
    public RedirectView register(TeamDto teamDto) {
        log.debug("Register team " + ":" + teamDto);
        Team team = teamService.registerNewTeam(teamDto);
        return new RedirectView(String.format("/team/%d", team.getId()), false, false);
    }

    @GetMapping(value = "/team/{teamId:\\d+}", produces = MediaType.TEXT_HTML_VALUE)
    public String profile(@PathVariable int teamId, Model model) {
        Optional<Team> team = teamService.findById(teamId);
        if (!team.isPresent()) {
            throw new ResourceNotFoundException("team.id.not_found", teamId);
        }

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Team) {
            model.addAttribute("readOnly", ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId() != teamId);
        }

        model.addAttribute("team", team.get());
        model.addAttribute("formAction", String.format("/team/%d", teamId));
        return "team/profile";
    }

    @PutMapping(value = "/team/{teamId:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> update(@PathVariable int teamId, TeamDto teamDto) {
        log.debug("Update team " + teamId + ": " + teamDto);
        teamService.updateTeam(teamId, teamDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("team.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/team/{teamId:\\d+}/reports")
    //TODO
    public ResponseEntity<Model> showReports(@PathVariable int teamId, Model model) {
        List<Report> reports = reportService.findByTeamId(teamId);
        model.addAttribute("reports", reports);
        model.addAttribute("formAction", String.format("/team/%d/reports", teamId));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/team/{teamId:\\d+}/review/{stageId:\\d+}")
    //TODO
    public ResponseEntity<Model> showReviewByTeamAndStage(@PathVariable int teamId, @PathVariable int stageId, Model model) {
        List<Review> reviews = reviewService.findByTeamIdAndStageId(teamId, stageId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("formAction", String.format("/team/%d/reports/review/%d", teamId, stageId));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/team/{teamId:\\d+}/report/{stageId:\\d+}")
    public RedirectView showSelectedReport(@PathVariable int teamId, @PathVariable int stageId, RedirectAttributes redirectAttributes) {
        Optional<Report> report = reportService.find(teamId, stageId);
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("report.team_stage.not_found", String.format("%d, %d", teamId, stageId));
        }

        redirectAttributes.addFlashAttribute("report", report.get());
        final RedirectView redirectView = new RedirectView(String.format("/report/%d", report.get().getId()), false, false);
        redirectView.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        return redirectView;
    }

}
