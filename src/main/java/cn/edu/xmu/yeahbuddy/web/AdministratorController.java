package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.*;
import cn.edu.xmu.yeahbuddy.service.*;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.util.Pair;
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
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        model.addAttribute("admin", administrator.get());
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
        result.put("message", messageSource.getMessage("admin.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/task/create")
    @PreAuthorize("hasAuthority('ManageTask')")
    public String createTask(Model model) {
        List<Team> teams = teamService.findAllTeams();

        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Stage> stages = stageService.findByEndAfter(current);
        model.addAttribute("teams", teams);
        model.addAttribute("stages", stages);
        model.addAttribute("formAction", "/task/create");
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        return "admin/taskCreate";
    }

    @PostMapping("/task/create")
    @PreAuthorize("hasAuthority('ManageTask')")
    public RedirectView createTask(StageCreationDto dto, Model model) {
        List<Team> teams = dto.getTeamChosen().stream().map(teamService::loadById).collect(Collectors.toList());
        Stage stage = stageService.createStage(dto.getId(), new StageDto().setStart(dto.getStart()).setEnd(dto.getEnd()).setTitle(dto.getTitle()));
        teams.forEach(team -> reportService.createReport(team, stage, dto.getTitle()));
        return new RedirectView("/task/create", false, false);
    }

    @GetMapping("/task/history")
    @PreAuthorize("hasAuthority('ManageTask')")
    public String taskHistory(Model model) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Stage> stages = stageService.findByEndBefore(current);

        model.addAttribute("stages", stages);
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/taskHistory";
    }

    //TODO: 来自taskCreate中的 详情 和 修改 按钮(详情与修改链接到的一样)（OK）
    @GetMapping("/task/{stageId:\\d+}/detail")
    @PreAuthorize("hasAuthority('ManageTask')")
    public String taskDetail(@PathVariable int stageId, Model model) {
        Optional<Stage> stage = stageService.findById(stageId);
        if (!stage.isPresent()) {
            throw new ResourceNotFoundException("stage.id.not_found", stageId);
        }

        List<Report> reports = reportService.findByStage(stage.get());
        model.addAttribute("stage", stage.get());
        model.addAttribute("reports", reports);
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        return "admin/taskDetail";
    }

    //TODO:修改任务的信息:stageService的更新未写


    //TODO:创建token+显示所有没有失效的token（OK）
    @GetMapping("/token/create/{stageId:\\d+}")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String createToken(@PathVariable("stageId") int stageId, Model model) {
        Stage stage = stageService.loadById(stageId);
        List<Team> teams = resultService.findBySubmittedFalse()
                                        .stream().filter(result -> result.getStage().equals(stage))
                                        .filter(result -> reviewService.findByReport(result.getReport()).isEmpty())
                                        .map(Result::getTeam)
                                        .collect(Collectors.toList());

        model.addAttribute("stage", stage);
        model.addAttribute("teams", teams);
        model.addAttribute("tutors", tutorService.findAllTutors());
        model.addAttribute("formAction", String.format("/token/create/%d", stageId));
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/tokenCreate";
    }

    @PostMapping("/token/create/{stageId:\\d+}")
    @PreAuthorize("hasAuthority('ManageToken')")
    public RedirectView createToken(@PathVariable("stageId") int stageId, TokenCreationDto dto, Model model) {
        Stage stage = stageService.loadById(stageId);
        List<Team> teams = dto.getTeamChosen().stream().map(teamService::loadById).collect(Collectors.toList());
        List<Report> reports = teams.stream().map(team -> reportService.find(team, stage)).flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty)).collect(Collectors.toList());
        List<Tutor> tutors = dto.getTutorChosen().stream().map(tutorService::loadById).collect(Collectors.toList());
        List<Pair<Tutor, Set<Review>>> list = tutors.stream().map(
                tutor -> Pair.of(tutor, reports.stream().map(report -> reviewService.createReview(report, tutor)).collect(Collectors.toSet()))
        ).collect(Collectors.toList());
        list.forEach(
                pair -> tokenService.createToken(pair.getFirst(), pair.getSecond(), dto.getEnd())
        );
        return new RedirectView(String.format("/token/create/%d", stageId), false, false);
    }

    @GetMapping("/token/current")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String currentTokens(Model model) {
        model.addAttribute("tokens", tokenService.findByNotRevoked());
        model.addAttribute("stages", stageService.findByEndAfter(Timestamp.from(Instant.now())));
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/tokenCurrent";
    }

    //TODO:获取所有已截止的token（OK）
    @GetMapping("/token/history")
    @PreAuthorize("hasAuthority('ManageToken')")
    public String allTokens(Model model) {
        model.addAttribute("tokens", tokenService.findByRevoked());
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/tokenHistory";
    }

    @GetMapping("/result/current")
    @PreAuthorize("hasAuthority('ViewReview')")
    public String reportViewAndResult(Model model) {
        List<Result> results = resultService.findBySubmittedFalse();
        Map<Integer, String> stat = new HashMap<>();
        results.stream()
               .map(result -> Pair.of(result, reviewService.findByReport(result.getReport())))
               .forEach(pair -> {
                   String status = String.format("%d/%d", pair.getSecond().stream().filter(Review::isSubmitted).count(), pair.getSecond().size());
                   stat.put(pair.getFirst().getId(), status);

               });
        model.addAttribute("results", results);
        model.addAttribute("reviewStat", stat);
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/results";
    }

    @GetMapping("/result/history")
    @PreAuthorize("hasAuthority('ViewReport')")
    public String reportHistory(Model model) {
        List<Result> results = resultService.findBySubmittedTrue();
        model.addAttribute("results", results);
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/reportHistory";
    }

    @GetMapping("/result/{resultId:\\d+}")
    @PreAuthorize("hasAuthority('SetResult')")
    public String result(@PathVariable int resultId, Model model) {
        Optional<Result> result = resultService.findById(resultId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("result.id.not_found", resultId);
        }

        List<Review> reviews = reviewService.findByReport(result.get().getReport()).stream().filter(Review::isSubmitted).collect(Collectors.toList());

        model.addAttribute("reviews", reviews);
        model.addAttribute("result", result.get());
        model.addAttribute("report", result.get().getReport());
        model.addAttribute("team", result.get().getTeam());
        model.addAttribute("formAction", String.format("/result/%d", resultId));
        model.addAttribute("adminId",  ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        return "admin/result";
    }

    @PutMapping("/result/{resultId:\\d+}")
    @PreAuthorize("hasAuthority('SetResult')")
    public ResponseEntity<Map<String, String>> updateResult(@PathVariable int resultId, ResultDto resultDto, Model model) {
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

    @GetMapping("/admin/tutor")
    public String adminTutors(Model model){
        List<Tutor> tutors = tutorService.findAllTutors();
        model.addAttribute("tutors",tutors);
        model.addAttribute("adminId", ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        return "admin/tutorAdmin";
    }

    @GetMapping("/admin/team")
    public String adminTeams(Model model){
        List<Team> teams = teamService.findAllTeams();
        model.addAttribute("teams",teams);
        model.addAttribute("adminId", ((Administrator) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        return "admin/teamAdmin";
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
        model.addAttribute("adminId", adminId);
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
        model.addAttribute("adminId", administrator.get().getId());
        model.addAttribute("formAction", String.format("/admin/%d/password", adminId));
        return "admin/password";
    }
}
