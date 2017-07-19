package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.*;
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

    private final TutorService tutorService;

    private final ReviewService reviewService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService, ReportService reportService, TokenService tokenService, TeamService teamService, TutorService tutorService, ReviewService reviewService, MessageSource messageSource) {
        this.administratorService = administratorService;
        this.reportService = reportService;
        this.tokenService = tokenService;
        this.teamService = teamService;
        this.tutorService = tutorService;
        this.reviewService = reviewService;
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

    //TODO:创建任务＋显示所有没有截止的项目报告任务
    @GetMapping("/task/create")
    @PreAuthorize("hasAuthority('CreateTask')")
    public String createTask(Model model){
        List<Team> teams = teamService.findAllTeams();

        model.addAttribute("teams",teams);
        return "admin/reportTaskCreate";
    }

    //TODO:获取所有已经截止的项目报告任务(还没写)
    @GetMapping("task/history")
    public String taskHistory(Model model){

        return "admin/reportTaskHistory";
    }

    //TODO:reportTaskDetail.html 未写, reportTaskCreate中的 详情 与 修改 按钮需要更改，有待商榷
    @GetMapping("/task/{stageId:\\d+}")
    public String taskDetail(@PathVariable int stageId, Model model){

        return "admin/reportTaskDetail";
    }

    //TODO:创建token+显示所有没有失效的token(还没有写需要导师来评审的报告,即没有被综合评审好的报告)
    @GetMapping("/token/create")
    public String createToken(Model model){

        List<Tutor> tutors = tutorService.findAllTutors();
        List<Token> tokens = tokenService.findByRevokedIsFalse();

        model.addAttribute("tutors",tutors);
        model.addAttribute("tokens",tokens);
        return "admin/tokenCreate";
    }

    //TODO:获取所有已截止的token
    @GetMapping("/token")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String allTokens(Model model) {
        List<Token> tokens = tokenService.findByRevokedIsTrue();
        model.addAttribute("tokens", tokens);
        return "admin/tokenHistory";
    }

    //TODO:reportTokenDetail.html 未写, tokenCreate中的 详情 与 修改 按钮需要更改，有待商榷
    @GetMapping("/token/{tokenId:\\d+}")
    public String tokenDetail(@PathVariable int tokenId, Model model){

        return "admin/reportTaskDetail";
    }

    //TODO:获取所有未综合评审完的项目报告(和result有关，暂时没写)
    @GetMapping("/report/result")
    public String reportViewAndResult(Model model){

        return "admin/reportViewAndResult";
    }

    //TODO:获取所有综合评审完的项目报告
    @GetMapping("/report/history")
    @PreAuthorize("hasAuthority('ViewReport')")
    public String reportHistory(Model model) {
        List<Report> reports = reportService.findAllReports();
        Set<Team> teams = reports.stream().map(Report::getTeamId).distinct().map(teamService::loadById).collect(Collectors.toSet());
        model.addAttribute("reports", reports);
        model.addAttribute("teams", teams);
        return "admin/reportHistory";
    }

    //TODO:获取某个项目报告的内容和所有评审结果(还没加上返回综合评审结果:result)
    @GetMapping("/report/{reportId:\\d+}")
    public String reportResult(@PathVariable int reportId, Model model){
        Optional<Report> report = reportService.findById(reportId);
        if(!report.isPresent()){
            throw new ResourceNotFoundException("report.id.not_found", reportId);
        }

        List<Review> reviews = reviewService.findByReport(report.get());

        model.addAttribute("report",report.get());
        model.addAttribute("reviews",reviews);
        model.addAttribute("formAction", String.format("/report/%d", reportId));
        return "admin/reportResult";
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
