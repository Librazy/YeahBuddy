package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


@Entity
public class Review {

    @EmbeddedId
    private ReviewKey reviewKey;

    @Column(name = "ReviewRank", nullable = false)
    private int rank;

    @Column(name = "ReviewText")
    private String text;

    @Column(name = "ReviewSubmitted", nullable = false)
    private boolean submitted;

    public Review() {
    }

    public Review(ReviewKey reviewKey) {
        this.reviewKey = reviewKey;
    }

    public Review(int teamId, int stage, int viewer, boolean viewerIsAdmin) {
        this.reviewKey = new ReviewKey(teamId, stage, viewer, viewerIsAdmin);
    }

    @Contract(pure = true)
    public ReviewKey getReviewKey() {
        return reviewKey;
    }

    @Contract(pure = true)
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Contract(pure = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Contract(pure = true)
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
