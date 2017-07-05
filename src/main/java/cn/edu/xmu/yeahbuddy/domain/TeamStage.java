package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TeamStage implements Serializable {

    private static final long serialVersionUID = -9005675776332115962L;

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

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return (teamId << 16) + stage;
    }

    @Contract(pure = true)
    @Override
    public boolean equals(Object t) {
        return t instanceof TeamStage && ((TeamStage) t).getTeamId() == this.getTeamId() && ((TeamStage) t).getStage() == this.getStage();
    }

    @Contract(pure = true)
    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Contract(pure = true)
    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return String.format("teamId:%d stage:%d", teamId, stage);
    }
}
