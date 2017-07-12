package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.Collection;
import java.util.Map;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"ReportTeamId", "ReportStageId"})
)
@Entity
public class Report {

    @Id
    @GeneratedValue
    @Column(name = "ReviewId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "ReportTeamId", updatable = false, nullable = false)
    private int teamId;

    @Column(name = "ReportStageId", updatable = false, nullable = false)
    private int stageId;

    @Column(name = "ReportSubmitted", nullable = false)
    private boolean submitted;

    @Column(name = "ReportTitle", nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReportContent")
    private Map<Integer, String> content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ReportFiles")
    private Collection<String> files;

    public Report() {
    }

    public Report(int teamId, int stageId) {
        this.teamId = teamId;
        this.stageId = stageId;
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
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public void setContent(Map<Integer, String> content) {
        this.content = content;
    }

    @Contract(pure = true)
    public Collection<String> getFiles() {
        return files;
    }

    public void setFiles(Collection<String> files) {
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
