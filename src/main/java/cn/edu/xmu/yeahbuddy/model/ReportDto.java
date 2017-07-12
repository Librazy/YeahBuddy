package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class ReportDto implements Serializable {

    private static final long serialVersionUID = -4399048744984741761L;

    private Boolean submitted;

    private String title;

    private Map<Integer, String> content;

    private Collection<String> files;

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
    public Map<Integer, String> getContent() {
        return content;
    }

    public ReportDto setContent(Map<Integer, String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Collection<String> getFiles() {
        return files;
    }

    public ReportDto setFiles(Collection<String> files) {
        this.files = files;
        return this;
    }
}
