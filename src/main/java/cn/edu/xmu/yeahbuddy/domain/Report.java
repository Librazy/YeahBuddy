package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"ReportTeamId", "ReportStageId"})
)
@Entity
public class Report {

    @Id
    @GeneratedValue
    @Column(name = "ReportId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @NaturalId
    @ManyToOne
    @JoinColumn(name = "ReportTeamId", updatable = false, nullable = false)
    private Team team;

    @NaturalId
    @ManyToOne
    @JoinColumn(name = "ReportStageId", updatable = false, nullable = false)
    private Stage stage;

    @Column(name = "ReportSubmitted", nullable = false)
    private boolean submitted;

    @Column(name = "ReportTitle", nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReportContent")
    private List<String> content = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReportFiles")
    private Set<String> files;

    public Report() {
    }

    public Report(Team team, Stage stage) {
        this.team = team;
        this.stage = stage;
        this.content.add("");
        this.content.add("");
        this.content.add("");
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public int getTeamId() {
        return getTeam().getId();
    }

    @Contract(pure = true)
    public Team getTeam() {
        return team;
    }

    @Contract(pure = true)
    public int getStageId() {
        return getStage().getId();
    }

    @Contract(pure = true)
    public Stage getStage() {
        return stage;
    }

    @Contract(pure = true)
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Contract(pure = true)
    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    @Contract(pure = true)
    public Set<String> getFiles() {
        return files;
    }

    public void setFiles(Set<String> files) {
        this.files = files;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
