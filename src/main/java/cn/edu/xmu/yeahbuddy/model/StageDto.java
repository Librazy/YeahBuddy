package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.sql.Timestamp;

public class StageDto {

    private Timestamp start;

    private Timestamp end;

    private String description;

    private String title;

    @Contract(pure = true)
    public Timestamp getStart() {
        return start;
    }

    public StageDto setStart(Timestamp start) {
        this.start = start;
        return this;
    }

    @Contract(pure = true)
    public Timestamp getEnd() {
        return end;
    }

    public StageDto setEnd(Timestamp end) {
        this.end = end;
        return this;
    }

    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    public StageDto setDescription(String description) {
        this.description = description;
        return this;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    public StageDto setTitle(String title) {
        this.title = title;
        return this;
    }
}
