package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.util.Map;

public class ResultDto {

    private Integer rank;

    private Map<Integer, String> content;

    private Boolean submitted;

    @Contract(pure = true)
    public Integer getRank() {
        return rank;
    }

    public ResultDto setRank(Integer rank) {
        this.rank = rank;
        return this;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public ResultDto getContent(Map<Integer, String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Boolean getSubmitted() {
        return submitted;
    }

    public ResultDto setSubmitted(Boolean submitted) {
        this.submitted = submitted;
        return this;
    }
}
