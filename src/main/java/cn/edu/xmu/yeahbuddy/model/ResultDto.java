package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.util.List;

public class ResultDto {

    private String brief;

    private List<String> content;

    private Boolean submitted;

    @Contract(pure = true)
    public String getBrief() {
        return brief;
    }

    public ResultDto setBrief(String brief) {
        this.brief = brief;
        return this;
    }

    @Contract(pure = true)
    public List<String> getContent() {
        return content;
    }

    public ResultDto setContent(List<String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Boolean getSubmitted() {
        return submitted;
    }

    public ResultDto setSubmitted(boolean submitted) {
        this.submitted = submitted;
        return this;
    }
}
