package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class TeamController {

    @NonNls
    private static Log log = LogFactory.getLog(TeamController.class);

    private final TeamService teamService;

    private final TeamReportService teamReportService;

    private final MessageSource messageSource;

    @Autowired
    public TeamController(TeamService teamService, TeamReportService teamReportService, MessageSource messageSource) {
        this.teamService = teamService;
        this.teamReportService = teamReportService;
        this.messageSource = messageSource;
    }

    @PostMapping("/team")
    public RedirectView register(TeamDto teamDto) {
        log.debug("Register team " + ":" + teamDto);
        Team team = teamService.registerNewTeam(teamDto);
        return new RedirectView(String.format("/team/%d", team.getId()), false, false);
    }

    @GetMapping("/team/{teamId:\\d+}")
    public String profile(@PathVariable int teamId, Model model) {
        Optional<Team> team = teamService.findByteamId(teamId);
        if (!team.isPresent()) {
            throw new ResourceNotFoundException("team.id.not_found", teamId);
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

}
