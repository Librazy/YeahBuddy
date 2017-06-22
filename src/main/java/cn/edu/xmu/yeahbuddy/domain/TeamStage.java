package cn.edu.xmu.yeahbuddy.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TeamStage implements Serializable {

    @Column(name = "TeamId", nullable = false)
    private int teamId;

    @Column(name = "Stage", nullable = false)
    private int stage;

    public TeamStage() {
    }

    public TeamStage(int teamId, int stage) {
        this.teamId = teamId;
        this.stage = stage;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }


    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
