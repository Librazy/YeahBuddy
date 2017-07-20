package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ReportDto implements Serializable {

    private static final long serialVersionUID = -4399048744984741761L;

    private Boolean submitted;

    private String title;

    private List<String> content;

    private Set<String> files;

    @Contract(pure = true)
    public Boolean getSubmitted() {
        return submitted;
    }

    public ReportDto setSubmitted(Boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    public ReportDto setTitle(String title) {
        this.title = title;
        return this;
    }

    @Contract(pure = true)
    public List<String> getContent() {
        return content;
    }

    public ReportDto setContent(List<String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Set<String> getFiles() {
        return files;
    }

    public ReportDto setFiles(Set<String> files) {
        this.files = files;
        return this;
    }
}
