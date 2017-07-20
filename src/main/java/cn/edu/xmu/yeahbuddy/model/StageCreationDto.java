package cn.edu.xmu.yeahbuddy.model;

import java.sql.Timestamp;
import java.util.Set;

public class StageCreationDto {

    private Timestamp start;

    private Timestamp end;

    private String title;

    private int id;

    private Set<Integer> teamChosen;

    public StageCreationDto() {
    }

    public StageCreationDto(Timestamp start, Timestamp end, String title, Set<Integer> teamChosen) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.teamChosen = teamChosen;
    }

    public int getId() {
        return id;
    }

    public StageCreationDto setId(int id) {
        this.id = id;
        return this;
    }

    public Timestamp getStart() {
        return start;
    }

    public StageCreationDto setStart(Timestamp start) {
        this.start = start;
        return this;
    }

    public Timestamp getEnd() {
        return end;
    }

    public StageCreationDto setEnd(Timestamp end) {
        this.end = end;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public StageCreationDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public Set<Integer> getTeamChosen() {
        return teamChosen;
    }

    public StageCreationDto setTeamChosen(Set<Integer> teamChosen) {
        this.teamChosen = teamChosen;
        return this;
    }
}
