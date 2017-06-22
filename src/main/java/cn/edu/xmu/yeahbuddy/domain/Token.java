package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;

@Entity
public class Token {

    @Id
    @Column(name = "TokenValue", updatable = false, nullable = false)
    private String tokenValue;

    @Column(name = "TokenTutorId", nullable = false)
    private String tutorId;

    @CreationTimestamp
    @Column(name = "TokenTime", nullable = false)
    private Time time;

    @Column(name = "TokenRevoked", nullable = false)
    private boolean revoked;

    @ElementCollection
    @CollectionTable(name = "TokenTeamIds")
    private Collection<String> teamIds;

    public Token() {
    }

    public Token(String tokenValue, String tutorId, Collection<String> teamIds) {
        this.tokenValue = tokenValue;
        this.tutorId = tutorId;
        this.teamIds = teamIds;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Collection<String> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Collection<String> teamIds) {
        this.teamIds = teamIds;
    }
}
