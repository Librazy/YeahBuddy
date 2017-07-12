package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.Map;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"ReviewTeamId", "ReviewStage", "ReviewViewer", "ReviewViewerIsAdmin"})
)
@Entity
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "ReviewId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "ReviewTeamId", updatable = false, nullable = false)
    private int teamId;

    @Column(name = "ReviewStage", updatable = false, nullable = false)
    private int stageId;

    @Column(name = "ReviewViewer", updatable = false, nullable = false)
    private int viewer;

    @Column(name = "ReviewViewerIsAdmin", updatable = false, nullable = false)
    private boolean viewerIsAdmin;

    @Column(name = "ReviewRank", nullable = false)
    private int rank = -1;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReviewContent")
    private Map<Integer, String> content;

    @Column(name = "ReviewSubmitted", nullable = false)
    private boolean submitted;

    public Review() {
    }

    public Review(int teamId, int stageId, int viewer, boolean viewerIsAdmin) {
        this.teamId = teamId;
        this.stageId = stageId;
        this.viewer = viewer;
        this.viewerIsAdmin = viewerIsAdmin;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public int getTeamId() {
        return teamId;
    }

    @Contract(pure = true)
    public int getStageId() {
        return stageId;
    }

    @Contract(pure = true)
    public int getViewer() {
        return viewer;
    }

    @Contract(pure = true)
    public boolean isViewerIsAdmin() {
        return viewerIsAdmin;
    }

    @Contract(pure = true)
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public void setContent(Map<Integer, String> content) {
        this.content = content;
    }

    @Contract(pure = true)
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
}
