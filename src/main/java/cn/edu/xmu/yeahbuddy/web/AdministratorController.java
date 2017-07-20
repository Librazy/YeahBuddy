package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.AdministratorDto;
import cn.edu.xmu.yeahbuddy.model.ResultDto;
import cn.edu.xmu.yeahbuddy.model.StageDto;
import cn.edu.xmu.yeahbuddy.service.*;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Timestamp;
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

    private final ResultService resultService;

    private final StageService stageService;

    private final MessageSource messageSource;

    @Autowired
    public AdministratorController(AdministratorService administratorService, ReportService reportService, TokenService tokenService, TeamService teamService, TutorService tutorService, ReviewService reviewService, ResultService resultService, StageService stageService, MessageSource messageSource) {
        this.administratorService = administratorService;
        this.reportService = reportService;
        this.tokenService = tokenService;
        this.teamService = teamService;
        this.tutorService = tutorService;
        this.reviewService = reviewService;
        this.resultService = resultService;
        this.stageService = stageService;
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

    //TODO:创建任务＋显示所有没有截止的项目报告任务(OK)
    @GetMapping("/task/create")
    @PreAuthorize("hasAuthority('CreateTask')")
    public String createTask(Model model) {
        List<Team> teams = teamService.findAllTeams();

        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Stage> stages = stageService.findByEndAfter(current);
        model.addAttribute("teams", teams);
        model.addAttribute("stages", stages);
        return "admin/taskCreate";
    }

    //TODO:获取所有已经截止的项目报告任务(OK)
    @GetMapping("/task/history")
    public String taskHistory(Model model) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Stage> stages = stageService.findByEndBefore(current);

        model.addAttribute("stages", stages);
        return "admin/taskHistory";
    }

    //TODO: 来自taskCreate中的 详情 和 修改 按钮(详情与修改链接到的一样)（OK）
    @GetMapping("/task/{stageId:\\d+}/detail")
    public String taskDetail(@PathVariable int stageId, Model model) {
        Optional<Stage> stage = stageService.findById(stageId);
        if (!stage.isPresent()) {
            throw new ResourceNotFoundException("stage.id.not_found", stageId);
        }

        List<Report> reports = reportService.findByStage(stage.get());
        model.addAttribute("stage", stage.get());
        model.addAttribute("reports", reports);
        return "admin/taskDetail";
    }

    //TODO:修改任务的信息:stageService的更新未写


    //TODO:创建token+显示所有没有失效的token（OK）
    @GetMapping("/token/create")
    public String createToken(Model model) {
        List<Report> reports = reportService.findAllReports();
        for (Report report : reports) {
            Optional<Result> result = resultService.findByReport(report);
            if (!result.isPresent())
                reports.remove(report);
            else if (result.get().isSubmitted())
                reports.remove(report);
        }
        List<Tutor> tutors = tutorService.findAllTutors();
        List<Token> tokens = tokenService.findByRevokedIsFalse();

        model.addAttribute("reports", reports);
        model.addAttribute("tutors", tutors);
        model.addAttribute("tokens", tokens);
        return "admin/tokenCreate";
    }

    //TODO:获取所有已截止的token（OK）
    @GetMapping("/token")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String allTokens(Model model) {
        List<Token> tokens = tokenService.findByRevokedIsTrue();
        model.addAttribute("tokens", tokens);
        return "admin/tokenHistory";
    }

    //TODO:获取所有未综合评审完的项目报告(OK)
    @GetMapping("/report/result")
    public String reportViewAndResult(Model model) {
        List<Result> results = resultService.findBySubmittedFalse();

        model.addAttribute("results", results);
        return "admin/reportViewAndResult";
    }

    //TODO:获取所有综合评审完的项目报告(OK)
    @GetMapping("/report/history")
    @PreAuthorize("hasAuthority('ViewReport')")
    public String reportHistory(Model model) {
        List<Result> results = resultService.findBySubmittedTrue();
        model.addAttribute("results", results);
        return "admin/reportHistory";
    }

    //TODO:获取某个项目报告的内容和所有评审结果(OK)
    @GetMapping("/report/{resultId:\\d+}/info")
    public String reportResult(@PathVariable int resultId, Model model) {
        Optional<Result> result = resultService.findById(resultId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("result.id.not_found", resultId);
        }

        List<Review> reviews = reviewService.findByReport(result.get().getReport());

        model.addAttribute("reviews", reviews);
        model.addAttribute("result", result.get());
        model.addAttribute("formAction", String.format("/report/%d/info", resultId));
        return "admin/reportResult";
    }

    //TODO:管理员进行综合评审
    @PutMapping("/report/{resultId:\\d+}/info")
    public ResponseEntity<Map<String, String>> updateReportResult(@PathVariable int resultId, ResultDto resultDto, Model model) {
        log.debug("Update result ");

        Optional<Result> reportResult = resultService.findById(resultId);
        if (!reportResult.isPresent()) {
            throw new ResourceNotFoundException("result.id.not_found", resultId);
        }

        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();

        resultService.updateResult(resultId, resultDto);

        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("result.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
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
