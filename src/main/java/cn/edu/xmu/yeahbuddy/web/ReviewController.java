package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.model.ReviewDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.ReviewService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.jetbrains.annotations.NonNls;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.*;

public class ReviewController {

    @NonNls
    private static Log log=LogFactory.getLog(ReviewController.class);

    private final ReviewService reviewService;

    private final ReportService reportService;

    private final TeamService teamService;

    private final MessageSource messageSource;

    @Autowired
    public ReviewController(ReviewService reviewService, ReportService reportService, TeamService teamService, MessageSource messageSource){
        this.reviewService=reviewService;
        this.reportService=reportService;
        this.teamService=teamService;
        this.messageSource=messageSource;
    }

    @GetMapping("/tutor/{tutorId:\\d+}/review/{reviewId:\\d+}")
    public String tutorReviewReport(@PathVariable int tutorId, @PathVariable int reviewId, Model model) {
        Optional<Review> review = reviewService.findById(reviewId);
        if (!review.isPresent()) {
            throw new ResourceNotFoundException("tutor.review.not_found", reviewId);
        }else if (!(review.get().getViewer() == tutorId && !review.get().isViewerIsAdmin())) {
            model.addAttribute("readOnly", true);
        }

        Optional<Report> report = reportService.find(review.get().getTeamId(), review.get().getStageId());
        if (!report.isPresent()) {
            throw new ResourceNotFoundException("team.report.not_found", String.format("%d, %d", review.get().getTeamId(), review.get().getStageId()));
        }

        Team team = teamService.loadById(report.get().getTeamId());

        model.addAttribute("team", team);
        model.addAttribute("report", report.get());
        model.addAttribute("review", review.get());
        return "tutor/reviewReport";
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

    @GetMapping("/team/{teamId:\\d+}/reports/review/{stageId:\\d+}")
    public String showReportReview(@PathVariable int teamId, @PathVariable int stageId, Model model) {
        List<Review> reviews = reviewService.findByTeamIdAndStageId(teamId, stageId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("formAction", String.format("/team/%d/reports/review/%d", teamId, stageId));
        return "team/reportReviews";
    }


}
