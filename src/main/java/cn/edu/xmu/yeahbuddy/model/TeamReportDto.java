package cn.edu.xmu.yeahbuddy.model;

import cn.edu.xmu.yeahbuddy.domain.TeamStage;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TeamReportDto implements Serializable {

    private static final long serialVersionUID = -4399048744984741761L;

    private TeamStage teamStage;

    private Boolean submitted;

    private String title;

    private Map<Integer, String> content;

    private Collection<String> files;

    @Contract(pure = true)
    public TeamStage getTeamStage() {
        return teamStage;
    }

    public TeamReportDto setTeamStage(TeamStage teamStage) {
        this.teamStage = teamStage;
        return this;
    }

    @Contract(pure = true)
    public Boolean getSubmitted() {
        return submitted;
    }

    public TeamReportDto setSubmitted(Boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    public TeamReportDto setTitle(String title) {
        this.title = title;
        return this;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public TeamReportDto setContent(Map<Integer, String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Collection<String> getFiles() {
        return files;
    }

    public TeamReportDto setFiles(Collection<String> files) {
        this.files = files;
        return this;
    }
}
