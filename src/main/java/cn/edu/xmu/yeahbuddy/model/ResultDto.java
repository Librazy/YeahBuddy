package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.util.Map;

public class ResultDto {

    private String brief;

    private Map<Integer, String> content;

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
    public Map<Integer, String> getContent() {
        return content;
    }

    public ResultDto setContent(Map<Integer, String> content) {
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
