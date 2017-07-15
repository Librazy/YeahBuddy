package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.*;
import cn.edu.xmu.yeahbuddy.model.ReviewDto;
import cn.edu.xmu.yeahbuddy.model.TutorDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.ReviewService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.service.TutorService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class TutorController {

    @NonNls
    private static Log log = LogFactory.getLog(TutorController.class);

    private final TutorService tutorService;

    private final ReviewService reviewService;

    private final TeamService teamService;

    private final ReportService reportService;

    private final MessageSource messageSource;

    @Autowired
    public TutorController(TutorService tutorService, ReviewService reviewService, TeamService teamService, ReportService reportService, MessageSource messageSource) {
        this.tutorService = tutorService;
        this.reviewService = reviewService;
        this.teamService = teamService;
        this.reportService = reportService;
        this.messageSource = messageSource;
    }

    @GetMapping("/tutor/login")
    public String tutorLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "tutor/login";
    }

    @GetMapping({"/tutor", "/tutor/"})
    @PreAuthorize("hasRole('TUTOR')")
    public String tutor(Model model) {
        String phone = ((Tutor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPhone();
        model.addAttribute("name", phone);
        return "tutor/index";
    }

    @GetMapping("/tutor/{tutorId:\\d+}/reviews")
    @PreAuthorize("hasRole('TUTOR')")
    //TODO
    public ResponseEntity<List<Pair<Team, Pair<Integer, Integer>>>> tutorReview(@PathVariable int tutorId, Model model) {
        Token token = (Token) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        List<Pair<Team, Pair<Integer, Integer>>> resultList = new ArrayList<>();
        for (int teamId : token.getTeamIds()) {
            Optional<Team> team = teamService.findById(teamId);
            if (!team.isPresent()) continue;

            Optional<Review> review = reviewService.find(teamId, token.getStage(), tutorId, false);
            if (!review.isPresent()) continue;
            Pair<Team, Pair<Integer, Integer>> result = Pair.of(team.get(), Pair.of(token.getStage(), review.get().getId()));
            resultList.add(result);
        }
        model.addAttribute("reportsReview", resultList);
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/review/{reviewId:\\d+}")
    //TODO
    public ResponseEntity<Model> tutorReviewReport(@PathVariable int reviewId, Model model) {
        Optional<Review> review = reviewService.findById(reviewId);
        if (!review.isPresent()) {
            throw new ResourceNotFoundException("tutor.review.not_found", reviewId);
        } else {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Tutor) {
                int tutorId = ((Tutor) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
                if (!(review.get().getViewer() == tutorId && !review.get().isViewerIsAdmin())) {
                    model.addAttribute("readOnly", true);
                }
            }
        }

        Optional<Report> report = reportService.find(review.get().getTeamId(), review.get().getStageId());
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("team.report.not_found", String.format("%d, %d", review.get().getTeamId(), review.get().getStageId()));
        }

        Team team = teamService.loadById(report.get().getTeamId());

        model.addAttribute("team", team);
        model.addAttribute("report", report.get());
        model.addAttribute("review", review.get());
        return ResponseEntity.ok(model);
    }

    @PutMapping("/tutor/{tutorId:\\d+}/review/{reviewId:\\d+}")
    public ResponseEntity<Map<String, String>> updateReview(@PathVariable int tutorId, @PathVariable int reviewId, ReviewDto reviewDto) {
        log.debug("Update Review");
        Optional<Review> review = reviewService.findById(reviewId);

        if (!review.isPresent()) {
            throw new ResourceNotFoundException("tutor.review.not_found", reviewId);
        } else if (!(review.get().getViewer() == tutorId && !review.get().isViewerIsAdmin())) {
            throw new AccessDeniedException("tutor.review.not_owned");
        }

        reviewService.updateReview(reviewId, reviewDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("review.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tutor/{tutorId:\\d+}")
    public String profile(@PathVariable int tutorId, Model model) {
        Tutor tutor = tutorService.loadById(tutorId);
        model.addAttribute("tutor", tutor);
        model.addAttribute("formAction", String.format("/tutor/%d", tutorId));
        return "tutor/profile";
    }

    @PutMapping("/tutor/{tutorId:\\d+}")
    public ResponseEntity<Map<String, String>> update(@PathVariable int tutorId, TutorDto tutorDto) {
        log.debug("Update Tutor " + tutorId + ": " + tutorDto);
        tutorService.updateTutor(tutorId, tutorDto);
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("tutor.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }
}
