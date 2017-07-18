package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.stream.Collectors;


//TODO: 每个阶段每个导师是否至多一个token？
@Entity
public class Token {

    @Id
    @Column(name = "TokenValue", updatable = false, nullable = false)
    private String tokenValue;

    @ManyToOne
    @JoinColumn(name = "TokenTutorId", updatable = false, nullable = false)
    private Tutor tutor;

    @ManyToOne
    @JoinColumn(name = "TokenStage", updatable = false, nullable = false)
    private Stage stage;

    @CreationTimestamp
    @Column(name = "TokenTime", updatable = false, nullable = false)
    private Time time;

    @Column(name = "TokenRevoked", nullable = false)
    private boolean revoked;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TokenTeamIds")
    private Collection<Integer> teamIds;

    public Token() {
    }

    public Token(String tokenValue, Tutor tutor, Stage stage, Collection<Integer> teamIds) {
        this.tokenValue = tokenValue;
        this.tutor = tutor;
        this.stage = stage;
        this.teamIds = teamIds;
    }

    @Contract(pure = true)
    public String getTokenValue() {
        return tokenValue;
    }

    @Contract(pure = true)
    public Tutor getTutor() {
        return tutor;
    }

    @Contract(pure = true)
    public int getTutorId() {
        return getTutor().getId();
    }

    @Contract(pure = true)
    public Stage getStage() {
        return stage;
    }

    @Contract(pure = true)
    public Time getTime() {
        return time;
    }

    @Contract(pure = true)
    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked() {
        this.revoked = true;
    }

    @Contract(pure = true)
    public Collection<Integer> getTeamIds() {
        return teamIds;
    }

    @Override
    public String toString() {
        return String.format("tokenValue:%s tutor:%s stage:%s revoked:%b teamIds:[%s]", tokenValue, tutor, stage, revoked, teamIds.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
