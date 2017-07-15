package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Administrator;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.service.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
public class AdministratorController {

    @NonNls
    private static Log log = LogFactory.getLog(AdministratorController.class);

    private final AdministratorService administratorService;

    private final TeamService teamService;

    private final TutorService tutorService;

    private final ReviewService reviewService;

    private final ReportService ReportService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService, TeamService teamService, TutorService tutorService, ReviewService reviewService, ReportService ReportService, MessageSource messageSource) {
        this.administratorService = administratorService;
        this.teamService = teamService;
        this.tutorService = tutorService;
        this.reviewService = reviewService;
        this.ReportService = ReportService;
        this.messageSource = messageSource;
    }

    @PreAuthorize("hasAuthority('ViewReport')")
    @GetMapping("/admin")
    public String admin(Model model) {
        String name = ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        log.debug("Administrator " + name + " viewed /admin");
        model.addAttribute("name", name);
        return "admin";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }

    @GetMapping("/admin/{adminId:\\d+}")
    public String profile(@PathVariable int adminId, Model model) {
        Optional<Administrator> administrator = administratorService.findById(adminId);
        if (!administrator.isPresent()) {
            throw new ResourceNotFoundException("administrator.id.not_found", adminId);
        }

        model.addAttribute("administrator", administrator);
        model.addAttribute("formAction", String.format("/admin/%d", adminId));
        return "administrator/profile";
    }

    @PutMapping(value = "/admin/{adminId:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> updateAdminInfo(@PathVariable int adminId, AdministratorDto administratorDto) {
        log.debug("Update administrator " + adminId + ": " + administratorDto);
        administratorService.updateAdministrator(adminId, administratorDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("administrator.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }
}
