package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.repo.ReviewRepository;
import cn.edu.xmu.yeahbuddy.model.ReviewDto;
import cn.edu.xmu.yeahbuddy.utils.IdentifierAlreadyExistsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param teamId        团队ID
     * @param stage         阶段
     * @param viewer        审核ID
     * @param viewerIsAdmin 审核者是否是管理员
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Review> find(int teamId, int stage, int viewer, boolean viewerIsAdmin) {
        log.debug("Finding Review");
        return reviewRepository.find(teamId, stage, viewer, viewerIsAdmin);
    }

    /**
     * 新建评审报告
     *
     * @param teamId        团队ID
     * @param stage         阶段
     * @param viewer        审核ID
     * @param viewerIsAdmin 审核者是否是管理员
     * @return 新注册的评审报告
     */
    @Transactional
    public Review createReview(int teamId, int stage, int viewer, boolean viewerIsAdmin) throws IdentifierAlreadyExistsException {
        log.debug(String.format("Trying to create Review: %d %d %d %b", teamId, stage, viewer, viewerIsAdmin));
        if (reviewRepository.find(teamId, stage, viewer, viewerIsAdmin).isPresent()) {
            log.info(String.format("Fail to create Review with id: %d %d %d %b: id already exist", teamId, stage, viewer, viewerIsAdmin));
            throw new IdentifierAlreadyExistsException("review.id.exist", null);
        }

        Review review = new Review(teamId, stage, viewer, viewerIsAdmin);
        reviewRepository.save(review);
        log.debug(String.format("Created new Review with id: %d %d %d %b", teamId, stage, viewer, viewerIsAdmin));
        return review;
    }

    /**
     * 删除团队评审报告
     *
     * @param id 评审报告主键
     */
    @Transactional
    public void deleteReview(int id) {
        log.debug("Delete TeamReport with id" + id);
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review updateReview(int id, ReviewDto dto) {
        log.debug("Trying to update Review with id" + id);
        Review review = reviewRepository.getOne(id);

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
