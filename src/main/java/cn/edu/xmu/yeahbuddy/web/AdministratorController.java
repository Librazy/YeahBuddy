package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.Token;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.service.AdministratorService;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.TokenService;
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
import java.util.stream.Collectors;

@Controller
public class AdministratorController {

    @NonNls
    private static Log log = LogFactory.getLog(AdministratorController.class);

    private final AdministratorService administratorService;

    private final ReportService reportService;

    private final TokenService tokenService;

    private final TeamService teamService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService, ReportService reportService, TokenService tokenService, TeamService teamService, MessageSource messageSource) {
        this.administratorService = administratorService;
        this.reportService = reportService;
        this.tokenService = tokenService;
        this.teamService = teamService;
        this.messageSource = messageSource;
    }

    @GetMapping("/admin")
    @PreAuthorize("T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal)")
    public RedirectView index() {
        int id = ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return new RedirectView(String.format("/admin/%d", id), false, false);
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "admin/login";
    }

    @GetMapping("/admin/{adminId:\\d+}")
    @PreAuthorize("hasAuthority('ManageAdministrator') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #adminId)")
    public String profile(@PathVariable int adminId, Model model) {
        Optional<Administrator> administrator = administratorService.findById(adminId);
        if (!administrator.isPresent()) {
            throw new ResourceNotFoundException("administrator.id.not_found", adminId);
        }

        model.addAttribute("administrator", administrator);
        model.addAttribute("formAction", String.format("/admin/%d", adminId));
        return "admin/profile";
    }

    @PutMapping(value = "/admin/{adminId:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ManageAdministrator') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #adminId)")
    public ResponseEntity<Map<String, String>> update(@PathVariable int adminId, AdministratorDto administratorDto) {
        log.debug("Update administrator " + adminId + ": " + administratorDto);
        administratorService.updateAdministrator(adminId, administratorDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("administrator.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    //TODO:
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('ViewReport')")
    public String allReports(Model model) {
        List<Report> reports = reportService.findAllReports();
        Set<Team> teams = reports.stream().map(Report::getTeamId).distinct().map(teamService::loadById).collect(Collectors.toSet());
        model.addAttribute("reports", reports);
        model.addAttribute("teams", teams);
        return "admin/ReportHistory";
    }

    @GetMapping("/token")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String allTokens(Model model) {
        List<Token> tokens = tokenService.findAllTokens();
        model.addAttribute("tokens", tokens);
        return "admin/TokenHistory";
    }

    @PostMapping(value = "/admin/{adminId:\\d+}/password", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAuthority('ManageAdministrator') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #adminId)")
    public String password(@PathVariable int adminId, @RequestParam Map<String, String> form, Model model) {
        String oldPassword = form.get("oldPassword");
        String newPassword = form.get("newPassword");
        try {
            administratorService.updateAdministratorPassword(adminId, oldPassword, newPassword);
            model.addAttribute("success", true);
        } catch (BadCredentialsException e) {
            model.addAttribute("passwordError", true);
        }
        model.addAttribute("formAction", String.format("/admin/%d/password", adminId));
        return "admin/password";
    }

    @GetMapping(value = "/admin/{adminId:\\d+}/password", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasAuthority('ManageAdministrator') " +
                          "|| (T(cn.edu.xmu.yeahbuddy.service.AdministratorService).isAdministrator(principal) && T(cn.edu.xmu.yeahbuddy.service.AdministratorService).asAdministrator(principal).id == #adminId)")
    public String password(@PathVariable int adminId, Model model) {
        Optional<Administrator> administrator = administratorService.findById(adminId);
        if (!administrator.isPresent()) {
            throw new ResourceNotFoundException("admin.id.not_found", adminId);
        }
        model.addAttribute("admin", administrator.get());
        model.addAttribute("formAction", String.format("/admin/%d/password", adminId));
        return "admin/password";
    }
}
