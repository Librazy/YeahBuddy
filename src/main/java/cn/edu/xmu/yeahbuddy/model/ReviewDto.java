package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Map;

public class ReviewDto implements Serializable {

    private static final long serialVersionUID = -5112892474273121041L;

    private Integer rank;

    private Map<Integer, String> content;

    private Boolean submitted;

    @Contract(pure = true)
    public Integer getRank() {
        return rank;
    }

    public ReviewDto setRank(Integer rank) {
        this.rank = rank;
        return this;
    }

    @Contract(pure = true)
    public Map<Integer, String> getContent() {
        return content;
    }

    public ReviewDto getContent(Map<Integer, String> content) {
        this.content = content;
        return this;
    }

    @Contract(pure = true)
    public Boolean getSubmitted() {
        return submitted;
    }

    public ReviewDto setSubmitted(Boolean submitted) {
        this.submitted = submitted;
        return this;
    }
}
