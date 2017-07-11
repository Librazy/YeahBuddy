package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.domain.TeamStage;
import cn.edu.xmu.yeahbuddy.domain.repo.ReviewRepository;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.model.TeamReportDto;
import cn.edu.xmu.yeahbuddy.service.TeamReportService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.*;

@Controller
public class TeamController {

    @NonNls
    private static Log log = LogFactory.getLog(TeamController.class);

    private final TeamService teamService;

    private final TeamReportService teamReportService;

    private final ReviewRepository reviewRepository;

    private final MessageSource messageSource;

    @Autowired
    public TeamController(TeamService teamService, TeamReportService teamReportService, ReviewRepository reviewRepository, MessageSource messageSource) {
        this.teamService = teamService;
        this.teamReportService = teamReportService;
        this.reviewRepository = reviewRepository;
        this.messageSource = messageSource;
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
        Optional<Team> team = teamService.findByteamId(teamId);
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
    public String showReports(@PathVariable int teamId, Model model) {
        List<TeamReport> teamReports = teamReportService.findByteamId(teamId);
        model.addAttribute("teamReports", teamReports);
        model.addAttribute("formAction", String.format("/team/%d/reports", teamId));
        return "team/reports";
    }

    @GetMapping("/team/{teamId:\\d+}/reports/{stage:\\d+}")
    public String showSelectedReport(@PathVariable int teamId, @PathVariable int stage, Model model) {
        Optional<TeamReport> teamReport = teamReportService.findById(new TeamStage(teamId, stage));
        if (!teamReport.isPresent()) {
            throw new ResourceNotFoundException("teamReport.stage.not_found", new TeamStage(teamId, stage));
        }

        model.addAttribute("teamReport", teamReport.get());
        model.addAttribute("formAction", String.format("/team/%d/reports/%d", teamId, stage));
        return "team/reportInformation";
    }

    @PutMapping("/team/{teamId:\\d+}/reports/{stage:\\d+}")
    public ResponseEntity<Map<String, String>> updateReport(@PathVariable int teamId, @PathVariable int stage, TeamReportDto teamReportDto) {
        log.debug("Update teamReport ");
        teamReportService.updateTeamReport(new TeamStage(teamId, stage), teamReportDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("teamReport.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/team/{teamId:\\d+}/reports/review/{stage:\\d+}")
    public String showReportReview(@PathVariable int teamId, @PathVariable int stage, Model model) {
        List<Review> reviews = reviewRepository.findByReviewKey_TeamIdAndReviewKey_Stage(teamId, stage);
        model.addAttribute("reviews", reviews);
        model.addAttribute("formAction", String.format("/team/%d/reports/review/%d", teamId, stage));
        return "team/reportReviews";
    }
}
