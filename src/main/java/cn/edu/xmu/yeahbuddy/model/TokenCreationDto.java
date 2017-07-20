package cn.edu.xmu.yeahbuddy.model;

import java.sql.Timestamp;
import java.util.Set;

public class TokenCreationDto {

    private Timestamp end;

    private Set<Integer> tutorChosen;

    private Set<Integer> teamChosen;

    public TokenCreationDto() {
    }

    public TokenCreationDto(Timestamp end, Set<Integer> tutorChosen, Set<Integer> teamChosen) {
        this.end = end;
        this.tutorChosen = tutorChosen;
        this.teamChosen = teamChosen;
    }

    public Timestamp getEnd() {
        return end;
    }

    public TokenCreationDto setEnd(Timestamp end) {
        this.end = end;
        return this;
    }

    public Set<Integer> getTutorChosen() {
        return tutorChosen;
    }

    public TokenCreationDto setTutorChosen(Set<Integer> tutorChosen) {
        this.tutorChosen = tutorChosen;
        return this;
    }

    public Set<Integer> getTeamChosen() {
        return teamChosen;
    }

    public TokenCreationDto setTeamChosen(Set<Integer> teamChosen) {
        this.teamChosen = teamChosen;
        return this;
    }
}
