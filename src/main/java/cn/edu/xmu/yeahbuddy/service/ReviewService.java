package cn.edu.xmu.yeahbuddy.service;

import cn.edu.xmu.yeahbuddy.domain.Review;
import cn.edu.xmu.yeahbuddy.domain.ReviewKey;
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
     * @param reviewKey 评审报告报告主键
     * @return 评审报告
     */
    @Transactional(readOnly = true)
    public Optional<Review> findById(ReviewKey reviewKey) {
        log.debug("Finding Review with key " + reviewKey);
        return reviewRepository.findById(reviewKey);
    }

    /**
     * 新建评审报告
     *
     * @param reviewKey 评审报告主键
     * @return 新注册的评审报告
     */
    @Transactional
    public Review createReview(ReviewKey reviewKey) throws IdentifierAlreadyExistsException {
        log.debug("Trying to create Review with id " + reviewKey);
        if (reviewRepository.findById(reviewKey).isPresent()) {
            log.info("Fail to create Review with id " + reviewKey + ": id already exist");
            throw new IdentifierAlreadyExistsException("review.id.exist");
        }

        Review review = new Review(reviewKey);
        reviewRepository.save(review);
        log.debug("Created new Review with id " + review.getReviewKey());
        return review;
    }

    /**
     * 删除团队评审报告
     *
     * @param id 评审报告主键
     */
    @Transactional
    public void deleteReview(ReviewKey id) {
        log.debug("Delete TeamReport with id" + id);
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review updateReview(ReviewKey id, ReviewDto dto) {
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
