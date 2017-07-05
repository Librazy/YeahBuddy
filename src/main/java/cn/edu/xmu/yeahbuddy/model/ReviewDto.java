package cn.edu.xmu.yeahbuddy.model;

import cn.edu.xmu.yeahbuddy.domain.ReviewKey;
import org.jetbrains.annotations.Contract;

import java.util.Map;

public class ReviewDto {

    private ReviewKey reviewKey;

    private int rank;

    private Map<Integer, String> content;

    private boolean submitted;

    @Contract(pure = true)
    public ReviewKey getReviewKey() {
        return reviewKey;
    }

    public ReviewDto setReviewKey(ReviewKey reviewKey) {
        this.reviewKey = reviewKey;
        return this;
    }

    @Contract(pure = true)
    public int getRank() {
        return rank;
    }

    public ReviewDto setRank(int rank) {
        this.rank = rank;
        return this;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public ReviewDto getContent(Map<Integer, String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public boolean getSubmitted() {
        return submitted;
    }

    public ReviewDto setSubmitted(boolean submitted) {
        this.submitted = submitted;
        return this;
    }
}
