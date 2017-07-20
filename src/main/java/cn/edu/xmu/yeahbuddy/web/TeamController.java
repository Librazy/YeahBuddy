package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.ReviewService;
import cn.edu.xmu.yeahbuddy.service.StageService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class TeamController {

    @NonNls
    private static Log log = LogFactory.getLog(TeamController.class);

    private final TeamService teamService;

    private final ReportService reportService;

    private final StageService stageService;

    private final MessageSource messageSource;

    private final ReviewService reviewService;

    @Autowired
    public TeamController(TeamService teamService, ReportService reportService, StageService stageService, MessageSource messageSource, ReviewService reviewService) {
        this.teamService = teamService;
        this.reportService = reportService;
        this.stageService = stageService;
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
    public RedirectView index() {
        int id = ((Team) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return new RedirectView(String.format("/team/%d", id), false, false);
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

    @PostMapping(value = "/team/{teamId:\\d+}/password", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAuthority('ManageTeam') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TeamService).isTeam(principal) && T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id == #teamId)")
    public String password(@PathVariable int teamId, @RequestParam Map<String, String> form, Model model) {
        String oldPassword = form.get("oldPassword");
        String newPassword = form.get("newPassword");
        try {
            teamService.updateTeamPassword(teamId, oldPassword, newPassword);
            model.addAttribute("success", true);
        } catch (BadCredentialsException e) {
            model.addAttribute("passwordError", true);
        }
        model.addAttribute("formAction", String.format("/team/%d/password", teamId));
        return "team/password";
    }

    @GetMapping(value = "/team/{teamId:\\d+}/password", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAuthority('ManageTeam') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.TeamService).isTeam(principal) && T(cn.edu.xmu.yeahbuddy.service.TeamService).asTeam(principal).id == #teamId)")
    public String password(@PathVariable int teamId, Model model) {
        Optional<Team> team = teamService.findById(teamId);
        if (!team.isPresent()) {
            throw new ResourceNotFoundException("team.id.not_found", teamId);
        }
        model.addAttribute("team", team.get());
        model.addAttribute("formAction", String.format("/team/%d/password", teamId));
        return "team/password";
    }

    @GetMapping("/team/{teamId:\\d+}/report")
    //TODO
    public ResponseEntity<Model> showReports(@PathVariable int teamId, Model model) {
        List<Report> reports = reportService.findByTeamId(teamId);
        model.addAttribute("reports", reports);
        model.addAttribute("formAction", String.format("/team/%d/reports", teamId));
        return ResponseEntity.ok(model);
    }
}
