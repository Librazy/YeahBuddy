package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
public class Token {

    @Id
    @Column(name = "TokenValue", updatable = false, nullable = false)
    private String tokenValue;

    @Column(name = "TokenTutorId", nullable = false)
    private int tutorId;

    @Column(name = "TokenStage", nullable = false)
    private int stage;

    @CreationTimestamp
    @Column(name = "TokenTime", nullable = false)
    private Time time;

    @Column(name = "TokenRevoked", nullable = false)
    private boolean revoked;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TokenTeamIds")
    private Collection<Integer> teamIds;

    public Token() {
    }

    public Token(String tokenValue, int tutorId, int stage, Collection<Integer> teamIds) {
        this.tokenValue = tokenValue;
        this.tutorId = tutorId;
        this.stage = stage;
        this.teamIds = teamIds;
    }

    @Contract(pure = true)
    public String getTokenValue() {
        return tokenValue;
    }

    @Contract(pure = true)
    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    @Contract(pure = true)
    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    @Contract(pure = true)
    public Time getTime() {
        return time;
    }

    @Contract(pure = true)
    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    @Contract(pure = true)
    public Collection<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Collection<Integer> teamIds) {
        this.teamIds = teamIds;
    }

    @Override
    public String toString() {
        return String.format("tokenValue:%s tutorId:%d stage:%d revoked:%b teamIds:[%s]", tokenValue, tutorId, stage, revoked, teamIds.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
