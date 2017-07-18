package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.Map;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"ReviewTeamId", "ReviewStageId", "ReviewViewer"})
)
@Entity
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "ReviewId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @NaturalId
    @ManyToOne
    @JoinColumn(name = "ReviewTeamId", updatable = false, nullable = false)
    private Team team;

    @NaturalId
    @ManyToOne
    @JoinColumn(name = "ReviewStageId", updatable = false, nullable = false)
    private Stage stage;

    @NaturalId
    @ManyToOne
    @JoinColumn(name = "ReviewViewer", updatable = false, nullable = false)
    private Tutor viewer;

    @Column(name = "ReviewRank", nullable = false)
    private int rank = -1;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReviewContent")
    private Map<Integer, String> content;

    @Column(name = "ReviewSubmitted", nullable = false)
    private boolean submitted;

    public Review() {
    }

    public Review(Team team, Stage stage, Tutor viewer) {
        this.team = team;
        this.stage = stage;
        this.viewer = viewer;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public Team getTeam() {
        return team;
    }

    @Contract(pure = true)
    public Stage getStage() {
        return stage;
    }

    @Contract(pure = true)
    public int getTeamId() {
        return getTeam().getId();
    }

    @Contract(pure = true)
    public int getStageId() {
        return getStage().getId();
    }

    @Contract(pure = true)
    public Tutor getViewer() {
        return viewer;
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
