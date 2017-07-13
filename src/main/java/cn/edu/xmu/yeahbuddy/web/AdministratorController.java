package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.service.*;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.jetbrains.annotations.NonNls;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

public class AdministratorController {

    @NonNls
    private static Log log=LogFactory.getLog(AdministratorController.class);

    private final AdministratorService administratorService;

    private final TeamService teamService;

    private final TutorService tutorService;

    private final ReviewService reviewService;

    private final ReportService ReportService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService,TeamService teamService, TutorService tutorService, ReviewService reviewService, ReportService ReportService, MessageSource messageSource){
        this.administratorService=administratorService;
        this.teamService=teamService;
        this.tutorService=tutorService;
        this.reviewService=reviewService;
        this.ReportService=ReportService;
        this.messageSource=messageSource;
    }

    @GetMapping("/admin/{adminId:\\d+}")
    public String profile(@PathVariable int adminId, Model model){
        Optional<Administrator> administrator=administratorService.findById(adminId);
        if(!administrator.isPresent()){
            throw new ResourceNotFoundException("administrator.id.not_found", adminId);
        }

        model.addAttribute("administrator",administrator);
        model.addAttribute("formAction",String.format("/admin/%d",adminId));
        return "administrator/profile";
    }

    @PutMapping(value = "/admin/{adminId:\\d+}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> updateAdminInfo(@PathVariable int adminId, AdministratorDto administratorDto){
        log.debug("Update administrator "+adminId+": "+administratorDto);
        administratorService.updateAdministrator(adminId, administratorDto);
        Map<String,String> result=new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("administrator.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/team")
    public String manageTeams(Model model){
        List<Team> teams=teamService.findAllTeams();
        model.addAttribute("teams", teams);
        return "administrator/manageteam";
    }

    @GetMapping("/admin/team/add")
    public String addTeamPage(Model model){
        return "administrator/addteam";
    }

    @PostMapping(value = "/admin/team/add")
    public RedirectView addTeam(TeamDto teamDto){
        log.debug("Add team");
        Team team = teamService.registerNewTeam(teamDto);
        return new RedirectView("/admin/manageteam/add", false, false);
    }

    @GetMapping("/admin/team/{teamId:\\d+}/update")
    public String updateTeamPage(@PathVariable int teamId, Model model){
        Team team = teamService.loadById(teamId);
        model.addAttribute("team",team);
        return "admin/updateteam";
    }

}
