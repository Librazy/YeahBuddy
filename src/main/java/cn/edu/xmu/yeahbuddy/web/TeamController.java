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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class TeamController {

    @NonNls
    private static Log log = LogFactory.getLog(TeamController.class);

    private final TeamService teamService;

    private final TeamReportService teamReportService;

    @Autowired
    public TeamController(TeamService teamService, TeamReportService teamReportService) {
        this.teamService = teamService;
        this.teamReportService = teamReportService;
    }

    @PostMapping("/team")
    public String register(TeamDto teamDto) {
        log.debug("Update team " + ":" + teamDto);
        Team team = teamService.registerNewTeam(teamDto);
        return String.format("redirect:team/%d", team.getId());
    }

    @GetMapping("/team/{teamId:\\d+}")
    public String showInformation(@PathVariable int teamId, Model model) {
        Optional<Team> team = teamService.findByteamId(teamId);
        if (team == null) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("team", team);
        model.addAttribute("formAction", String.format("/team/%d", teamId));
        return "team/information";
    }

    @PutMapping("/team/{teamId:\\d+}")
    public String updateInformation(@PathVariable int teamId, TeamDto teamDto) {
        log.debug("Update team " + teamId + ": " + teamDto);
        teamService.updateTeam(teamId, teamDto);
        return String.format("redirect:team/%d", teamId);
    }

    @GetMapping("/team/{teamId:\\d+}/reports")
    public String showReports(@PathVariable int teamId, Model model) {
        List<TeamReport> teamReports = teamReportService.findByteamId(teamId);
        model.addAttribute("teamReports", teamReports);
        model.addAttribute("formAction", String.format("/team/%d/reports", teamId));
        return "team/reports";
    }

}
