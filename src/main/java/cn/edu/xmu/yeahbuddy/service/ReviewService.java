package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Report;
import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.Tutor;
import cn.edu.xmu.yeahbuddy.domain.repo.ReviewRepository;
import cn.edu.xmu.yeahbuddy.model.ReviewDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import cn.edu.xmu.yeahbuddy.utils.IdentifierNotExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 评审服务
 */
@Service
public class ReviewService {

    @NonNls
    private static Log log = LogFactory.getLog(ReviewService.class);

    private final ReviewRepository reviewRepository;

    /**
     * 构造函数
     * Spring Boot自动装配
     *
     * @param reviewRepository Autowired
     */
    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * 查找评审报告
     *
     * @param id 评审报告报告主键
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Review> findById(int id) {
        log.debug("Finding Review with key " + id);
        return reviewRepository.findById(id);
    }

    /**
     * 查找评审报告
     *
     * @param report 报告
     * @param viewer 审核者
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Review> find(Report report, Tutor viewer) {
        log.debug("Finding Review");
        return reviewRepository.find(report, viewer);
    }

    /**
     * 查找某个团队项目报告的所有评审报告
     *
     * @param report 项目报告
     * @return 所有评审报告
     */
    @Transactional(readOnly = true)
    public List<Review> findByReport(Report report) {
        return reviewRepository.findByReport(report);
    }

    /**
     * 查找某导师的所有评审报告
     *
     * @param tutor 项目报告
     * @return 所有评审报告
     */
    @Transactional(readOnly = true)
    public List<Review> findByTutor(Tutor tutor) {
        return reviewRepository.findByTutor(tutor);
    }

    /**
     * 新建评审报告
     *
     * @param report 目标报告
     * @param viewer 审核导师
     * @return 新注册的评审报告
     */
    @Transactional
    public Review createReview(Report report, Tutor viewer) throws IdentifierAlreadyExistsException {
        log.debug(String.format("Trying to create Review: %s %s", report, viewer));
        if (reviewRepository.find(report, viewer).isPresent()) {
            log.info(String.format("Fail to create Review with id: %s %s: id already exist", report, viewer));
            throw new IdentifierAlreadyExistsException("review.id.exist", null);
        }

        Review review = new Review(report, viewer);
        review = reviewRepository.save(review);
        log.debug(String.format("Created new Review with id: %s %s", report, viewer));
        return review;
    }

    /**
     * 删除团队评审报告
     *
     * @param id 评审报告主键
     */
    @Transactional
    public void deleteReview(int id) {
        log.debug("Delete Report with id" + id);
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review updateReview(int id, ReviewDto dto) {
        log.debug("Trying to update Review with id" + id);
        Optional<Review> r = reviewRepository.queryById(id);

        if (!r.isPresent()) {
            log.info("Failed to load Report " + id + ": not found");
            throw new IdentifierNotExistsException("review.id.not_found", id);
        }
        Review review = r.get();
        if (dto.getSubmitted() != null) {
            log.trace("Updated submitted for Review with id " + id + ":" + review.isSubmitted() +
                              " -> " + dto.getSubmitted());
            review.setSubmitted(dto.getSubmitted());
        }

        if (dto.getContent() != null) {
            log.trace("Updated content for Review with id " + id);
            review.setContent(dto.getContent());
        }

        if (dto.getRank() != null) {
            log.trace("Updated submitted for Review with id " + id + ":" + review.getRank() +
                              " -> " + dto.getRank());
            review.setRank(dto.getRank());
        }

        return reviewRepository.save(review);
    }
}
