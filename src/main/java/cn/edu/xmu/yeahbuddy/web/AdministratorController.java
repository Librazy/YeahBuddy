package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
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

import java.util.*;

public class AdministratorController {

    @NonNls
    private static Log log=LogFactory.getLog(AdministratorController.class);

    private final AdministratorService administratorService;

    private final TeamService teamService;

    private final TutorService tutorService;

    private final ReportService reportService;

    private final TokenService tokenService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService,TeamService teamService, TutorService tutorService, ReportService reportService, TokenService tokenService, MessageSource messageSource){
        this.administratorService = administratorService;
        this.teamService = teamService;
        this.tutorService = tutorService;
        this.reportService = reportService;
        this.tokenService = tokenService;
        this.messageSource = messageSource;
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
        return "administrator/manageTeams";
    }

    @GetMapping("/admin/tutor")
    public String manageTutors(Model model){
        List<Tutor> tutors = tutorService.findAllTutors();
        model.addAttribute("tutors", tutors);
        return "administrator/manageTutors";
    }

    @GetMapping("/admin/report/create")
    public String createReport(Model model){
        List<Team> teams = teamService.findAllTeams();
        model.addAttribute("teams", teams);
        return "administrator/createReport";
    }

    @GetMapping("/admin/report/history")
    public String allReports(Model model){
        List<Report> reports = reportService.findAllReports();
        model.addAttribute("reports",reports);
        return "administrator/ReportHistory";
    }

    @GetMapping("/admin/token/create")
    public String createToken(Model model){
        List<Team> teams = teamService.findAllTeams();
        List<Tutor> tutors = tutorService.findAllTutors();
        model.addAttribute("teams", teams);
        model.addAttribute("tutors", tutors);
        return "administrator/createToken";
    }

    @GetMapping("/admin/token/history")
    public String allTokens(Model model){
        List<Token> tokens = tokenService.findAllTokens();
        model.addAttribute("tokens",tokens);
        return "administrator/TokenHistory";
    }
}
