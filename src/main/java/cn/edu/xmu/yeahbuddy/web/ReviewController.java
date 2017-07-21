package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.model.ReviewDto;
import cn.edu.xmu.yeahbuddy.service.ReportService;
import cn.edu.xmu.yeahbuddy.service.ReviewService;
import cn.edu.xmu.yeahbuddy.service.StageService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
public class ReviewController {

    @NonNls
    private static Log log = LogFactory.getLog(ReviewController.class);

    private final ReviewService reviewService;

    private final MessageSource messageSource;

    @Autowired
    public ReviewController(ReviewService reviewService, ReportService reportService, TeamService teamService, StageService stageService, MessageSource messageSource) {
        this.reviewService = reviewService;
        this.messageSource = messageSource;
    }

    @GetMapping("/review/{reviewId:\\d+}")
    @PreAuthorize("hasRole('TUTOR') && @reviewService.findById(#reviewId).get().tutor.id == T(cn.edu.xmu.yeahbuddy.service.TutorService).asTutor(principal).id")
    public String review(@PathVariable int reviewId, Model model) {
        Optional<Review> review = reviewService.findById(reviewId);
        if (!review.isPresent()) {
            throw new ResourceNotFoundException("tutor.review.not_found", reviewId);
        }

        model.addAttribute("team", review.get().getTeam());
        model.addAttribute("report", review.get().getReport());
        model.addAttribute("review", review.get());
        model.addAttribute("tutorId", review.get().getTutor().getId());

        if (review.get().isSubmitted()) {
            model.addAttribute("readOnly", true);
        }
        return "tutor/review";
    }

    @PutMapping("/review/{reviewId:\\d+}")
    @PreAuthorize("hasRole('TUTOR') && @reviewService.findById(#reviewId).get().tutor.id == T(cn.edu.xmu.yeahbuddy.service.TutorService).asTutor(principal).id")
    public ResponseEntity<Map<String, String>> update(@PathVariable int reviewId, ReviewDto reviewDto) {
        log.debug("Update Review");
        Optional<Review> review = reviewService.findById(reviewId);

        if (!review.isPresent()) {
            throw new ResourceNotFoundException("tutor.review.not_found", reviewId);
        }
        Map<String, String> result = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();

        if (review.get().isSubmitted()) {
            result.put("status", "409");
            result.put("error", messageSource.getMessage("http.status.409", new Object[]{}, locale));
            result.put("message", messageSource.getMessage("review.already.submitted", new Object[]{}, locale));
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }

        reviewService.updateReview(reviewId, reviewDto);

        result.put("status", messageSource.getMessage("response.ok", new Object[]{}, locale));
        result.put("message", messageSource.getMessage("review.update.ok", new Object[]{}, locale));
        return ResponseEntity.ok(result);
    }
}
