package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;


@Entity
@IdClass(ReviewKey.class)
public class Review {

    @Id
    @Column(name = "ReviewTeamStage", updatable = false, nullable = false)
    private TeamStage teamStage;

    @Id
    @Column(name = "ReviewViewer", nullable = false)
    private int viewer;

    @Id
    @Column(name = "ReviewViewerIsAdmin", nullable = false)
    private boolean viewerIsAdmin;

    @Column(name = "ReviewRank", nullable = false)
    private int rank;

    @Column(name = "ReviewText")
    private String text;

    @Column(name = "ReviewSubmitted", nullable = false)
    private boolean submitted;

    public Review(TeamStage teamStage, int viewer, boolean viewerIsAdmin) {
        this.teamStage = teamStage;
        this.viewer = viewer;
        this.viewerIsAdmin = viewerIsAdmin;
    }

    public TeamStage getTeamStage() {
        return teamStage;
    }

    public int getViewer() {
        return viewer;
    }

    public void setViewer(int viewer) {
        this.viewer = viewer;
    }

    public boolean isViewerIsAdmin() {
        return viewerIsAdmin;
    }

    public void setViewerIsAdmin(boolean viewerIsAdmin) {
        this.viewerIsAdmin = viewerIsAdmin;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
