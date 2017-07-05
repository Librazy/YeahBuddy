package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class TeamReport {

    @EmbeddedId
    private TeamStage teamStage;

    @Column(name = "TeamReportSubmitted", nullable = false)
    private boolean submitted;

    @Column(name = "TeamReportTitle", nullable = false)
    private String title;

    @Column(name = "TeamReportText")
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TeamReportFiles")
    private Collection<String> files;

    public TeamReport() {
    }

    public TeamReport(TeamStage teamStage) {
        this.teamStage = teamStage;
    }

    @Contract(pure = true)
    public TeamStage getTeamStage() {
        return teamStage;
    }

    @Contract(pure = true)
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Contract(pure = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
